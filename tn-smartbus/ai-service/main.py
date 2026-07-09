"""
TN SmartBus AI microservice.

Serves the trained ETA/delay/crowd models to the Spring Boot backend.
Endpoint contracts match what CrowdPredictionService / EtaPredictionService
already call (see backend/src/main/java/com/tnsmartbus/service/).

Run: uvicorn main:app --reload --port 8000
Models must exist first: python training/train_models.py
"""

from datetime import datetime
from typing import List, Optional

import joblib
import os
import pandas as pd
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

MODELS_DIR = os.path.join(os.path.dirname(__file__), "models")

app = FastAPI(
    title="TN SmartBus AI Service",
    description="ETA, delay, and crowd prediction for rural TNSTC buses",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

_models = {}


def load_model(name: str):
    path = os.path.join(MODELS_DIR, f"{name}.joblib")
    if not os.path.exists(path):
        return None
    return joblib.load(path)


@app.on_event("startup")
def startup():
    for name in ["crowd_model", "delay_model", "eta_model"]:
        _models[name] = load_model(name)
        if _models[name] is None:
            print(f"WARNING: {name}.joblib not found - run training/train_models.py first. "
                  f"Endpoints using this model will return a 503 until it's trained.")


def require_model(name: str):
    bundle = _models.get(name)
    if bundle is None:
        raise HTTPException(
            status_code=503,
            detail=f"{name} not trained yet. Run: python training/train_models.py",
        )
    return bundle


def derive_time_features(hour: int, day_of_week_name: Optional[str]):
    day_map = {"MONDAY": 0, "TUESDAY": 1, "WEDNESDAY": 2, "THURSDAY": 3,
               "FRIDAY": 4, "SATURDAY": 5, "SUNDAY": 6}
    dow = day_map.get((day_of_week_name or "").upper(), datetime.now().weekday())
    is_weekend = 1 if dow >= 5 else 0
    is_school_time = 1 if (7 <= hour <= 9 or 14 <= hour <= 16) else 0
    is_office_time = 1 if (8 <= hour <= 10 or 17 <= hour <= 19) else 0
    is_peak = max(is_school_time, is_office_time)
    return dow, is_weekend, is_peak


# ---------------------------------------------------------------------------
# Crowd prediction - contract matches CrowdPredictionService.callAiService()
# ---------------------------------------------------------------------------

class CrowdRequest(BaseModel):
    tripId: str
    hour: int
    dayOfWeek: str
    isRural: bool = True


class CrowdResponse(BaseModel):
    crowdLevel: str  # LOW, MEDIUM, HIGH
    confidence: float


@app.post("/predict/crowd", response_model=CrowdResponse)
def predict_crowd(request: CrowdRequest):
    bundle = require_model("crowd_model")
    model, features = bundle["model"], bundle["features"]

    dow, is_weekend, is_peak = derive_time_features(request.hour, request.dayOfWeek)
    row = pd.DataFrame([{
        "hour": request.hour,
        "day_of_week": dow,
        "is_weekend": is_weekend,
        "is_rural": int(request.isRural),
        "is_peak": is_peak,
    }])[features]

    prediction = model.predict(row)[0]
    probabilities = model.predict_proba(row)[0]
    confidence = float(max(probabilities))

    return CrowdResponse(crowdLevel=prediction, confidence=round(confidence, 3))


# ---------------------------------------------------------------------------
# ETA prediction - matches EtaPredictionService contract
# ---------------------------------------------------------------------------

class EtaRequest(BaseModel):
    distanceKm: float
    hour: Optional[int] = None
    isRural: bool = True
    rainFactor: float = 0.1


class EtaResponse(BaseModel):
    etaMinutes: float


@app.post("/predict/eta", response_model=EtaResponse)
def predict_eta(request: EtaRequest):
    bundle = require_model("eta_model")
    model, features = bundle["model"], bundle["features"]

    hour = request.hour if request.hour is not None else datetime.now().hour
    _, _, is_peak = derive_time_features(hour, None)

    row = pd.DataFrame([{
        "distance_km": request.distanceKm,
        "is_peak": is_peak,
        "is_rural": int(request.isRural),
        "rain_factor": request.rainFactor,
    }])[features]

    eta = float(model.predict(row)[0])
    return EtaResponse(etaMinutes=round(max(eta, 0.5), 1))


# ---------------------------------------------------------------------------
# Delay prediction
# ---------------------------------------------------------------------------

class DelayRequest(BaseModel):
    hour: Optional[int] = None
    isRural: bool = True
    rainFactor: float = 0.1


class DelayResponse(BaseModel):
    delayMinutes: float


@app.post("/predict/delay", response_model=DelayResponse)
def predict_delay(request: DelayRequest):
    bundle = require_model("delay_model")
    model, features = bundle["model"], bundle["features"]

    hour = request.hour if request.hour is not None else datetime.now().hour
    _, _, is_peak = derive_time_features(hour, None)

    row = pd.DataFrame([{
        "hour": hour,
        "is_peak": is_peak,
        "is_rural": int(request.isRural),
        "rain_factor": request.rainFactor,
    }])[features]

    delay = float(model.predict(row)[0])
    return DelayResponse(delayMinutes=round(max(delay, 0), 1))


# ---------------------------------------------------------------------------
# Route optimization - nearest-neighbour heuristic over stop coordinates.
# Not a trained model (route optimization is a combinatorial/TSP-style
# problem, not naturally a regression/classification task); this gives a
# genuinely useful ordering rather than a fake ML wrapper around it.
# ---------------------------------------------------------------------------

class StopPoint(BaseModel):
    stopName: str
    latitude: float
    longitude: float


class RouteOptimizeRequest(BaseModel):
    stops: List[StopPoint]


class RouteOptimizeResponse(BaseModel):
    orderedStopNames: List[str]
    totalDistanceKm: float


def haversine_km(lat1, lon1, lat2, lon2):
    from math import radians, sin, cos, sqrt, atan2
    R = 6371.0
    dlat, dlon = radians(lat2 - lat1), radians(lon2 - lon1)
    a = sin(dlat / 2) ** 2 + cos(radians(lat1)) * cos(radians(lat2)) * sin(dlon / 2) ** 2
    return R * 2 * atan2(sqrt(a), sqrt(1 - a))


@app.post("/optimize/route", response_model=RouteOptimizeResponse)
def optimize_route(request: RouteOptimizeRequest):
    if len(request.stops) < 2:
        raise HTTPException(status_code=400, detail="Need at least 2 stops to optimize")

    remaining = request.stops.copy()
    ordered = [remaining.pop(0)]  # fix the first stop as the route origin
    total_distance = 0.0

    while remaining:
        last = ordered[-1]
        nearest_idx = min(
            range(len(remaining)),
            key=lambda i: haversine_km(last.latitude, last.longitude,
                                        remaining[i].latitude, remaining[i].longitude),
        )
        nearest = remaining.pop(nearest_idx)
        total_distance += haversine_km(last.latitude, last.longitude, nearest.latitude, nearest.longitude)
        ordered.append(nearest)

    return RouteOptimizeResponse(
        orderedStopNames=[s.stopName for s in ordered],
        totalDistanceKm=round(total_distance, 2),
    )


@app.get("/health")
def health():
    return {
        "status": "ok",
        "modelsLoaded": {k: v is not None for k, v in _models.items()},
    }
