/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        base: "#0B1220",
        panel: "#121A2E",
        panel2: "#182236",
        border: "#232E47",
        muted: "#8592AD",
        ink: "#E7ECF5",
        signal: "#35D399",
        amber: "#F5A623",
        alert: "#EF5B5B",
      },
      fontFamily: {
        display: ["'Space Grotesk'", "sans-serif"],
        body: ["'Inter'", "sans-serif"],
        mono: ["'JetBrains Mono'", "monospace"],
      },
    },
  },
  plugins: [],
}
