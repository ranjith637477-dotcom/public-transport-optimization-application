"""
Trains the ETA, delay, and crowd prediction models for TN SmartBus.

There's no real TNSTC historical dataset available yet (gps_history and
crowd_data tables start empty), so this generates a synthetic dataset built
from realistic domain rules (school/office peak hours, rural-route slowdown,
weather effects) and trains real scikit-learn models on it. Once the
production database has accumulated real gps_history/crowd_data rows,
replace generate_synthetic_dataset() with a query against those tables and
re-run this script - main.py doesn't need to change at all.

Run: python training/train_models.py
Outputs: models/crowd_model.joblib, models/delay_model.joblib, models/eta_model.joblib
"""

import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, mean_absolute_error
import joblib
import os

np.random.seed(42)
N = 8000

OUTPUT_DIR = os.path.join(os.path.dirname(__file__), "..", "models")
os.makedirs(OUTPUT_DIR, exist_ok=True)


def generate_synthetic_dataset(n=N):
    hour = np.random.randint(0, 24, n)
    day_of_week = np.random.randint(0, 7, n)  # 0=Mon ... 6=Sun
    is_weekend = (day_of_week >= 5).astype(int)
    is_rural = np.random.binomial(1, 0.7, n)  # TN SmartBus focuses on rural routes
    distance_km = np.round(np.random.uniform(0.5, 8.0, n), 2)  # distance to next stop
    is_school_time = ((hour >= 7) & (hour <= 9) | (hour >= 14) & (hour <= 16)).astype(int)
    is_office_time = ((hour >= 8) & (hour <= 10) | (hour >= 17) & (hour <= 19)).astype(int)
    is_peak = np.maximum(is_school_time, is_office_time)
    rain_factor = np.random.beta(2, 5, n)  # mostly low, occasional heavy rain

    # ---- Crowd level (classification target) ----
    occupancy_score = (
        0.45 * is_peak
        + 0.15 * is_school_time
        + 0.10 * (1 - is_weekend)
        + 0.10 * is_rural  # fewer buses on rural routes -> more crowding per bus
        + np.random.normal(0, 0.12, n)
    )
    crowd_level = pd.cut(
        occupancy_score, bins=[-np.inf, 0.35, 0.6, np.inf], labels=["LOW", "MEDIUM", "HIGH"]
    ).astype(str)

    # ---- Delay in minutes (regression target) ----
    base_delay = (
        3.0
        + 8.0 * is_peak
        + 6.0 * rain_factor
        + 4.0 * is_rural * is_peak  # rural + peak compounds (single-lane roads, level crossings)
        + np.random.normal(0, 2.0, n)
    )
    delay_minutes = np.clip(base_delay, 0, None).round(1)

    # ---- ETA in minutes (regression target) ----
    avg_speed = 45 - 15 * is_peak - 10 * rain_factor - 5 * is_rural + np.random.normal(0, 3, n)
    avg_speed = np.clip(avg_speed, 8, None)
    eta_minutes = ((distance_km / avg_speed) * 60 + np.random.normal(0, 1.0, n)).clip(0.5, None).round(1)

    return pd.DataFrame({
        "hour": hour,
        "day_of_week": day_of_week,
        "is_weekend": is_weekend,
        "is_rural": is_rural,
        "is_peak": is_peak,
        "distance_km": distance_km,
        "rain_factor": rain_factor.round(2),
        "crowd_level": crowd_level,
        "delay_minutes": delay_minutes,
        "eta_minutes": eta_minutes,
    })


def train_crowd_model(df):
    features = ["hour", "day_of_week", "is_weekend", "is_rural", "is_peak"]
    X_train, X_test, y_train, y_test = train_test_split(
        df[features], df["crowd_level"], test_size=0.2, random_state=42
    )
    model = RandomForestClassifier(n_estimators=150, max_depth=8, random_state=42)
    model.fit(X_train, y_train)
    acc = accuracy_score(y_test, model.predict(X_test))
    print(f"Crowd model accuracy: {acc:.3f}")
    joblib.dump({"model": model, "features": features}, os.path.join(OUTPUT_DIR, "crowd_model.joblib"))


def train_delay_model(df):
    features = ["hour", "is_peak", "is_rural", "rain_factor"]
    X_train, X_test, y_train, y_test = train_test_split(
        df[features], df["delay_minutes"], test_size=0.2, random_state=42
    )
    model = RandomForestRegressor(n_estimators=150, max_depth=8, random_state=42)
    model.fit(X_train, y_train)
    mae = mean_absolute_error(y_test, model.predict(X_test))
    print(f"Delay model MAE: {mae:.2f} minutes")
    joblib.dump({"model": model, "features": features}, os.path.join(OUTPUT_DIR, "delay_model.joblib"))


def train_eta_model(df):
    features = ["distance_km", "is_peak", "is_rural", "rain_factor"]
    X_train, X_test, y_train, y_test = train_test_split(
        df[features], df["eta_minutes"], test_size=0.2, random_state=42
    )
    model = RandomForestRegressor(n_estimators=150, max_depth=8, random_state=42)
    model.fit(X_train, y_train)
    mae = mean_absolute_error(y_test, model.predict(X_test))
    print(f"ETA model MAE: {mae:.2f} minutes")
    joblib.dump({"model": model, "features": features}, os.path.join(OUTPUT_DIR, "eta_model.joblib"))


if __name__ == "__main__":
    print("Generating synthetic training dataset...")
    dataset = generate_synthetic_dataset()
    dataset.to_csv(os.path.join(OUTPUT_DIR, "synthetic_training_data_sample.csv"), index=False)

    print("Training crowd prediction model...")
    train_crowd_model(dataset)

    print("Training delay prediction model...")
    train_delay_model(dataset)

    print("Training ETA prediction model...")
    train_eta_model(dataset)

    print("All models trained and saved to", OUTPUT_DIR)
