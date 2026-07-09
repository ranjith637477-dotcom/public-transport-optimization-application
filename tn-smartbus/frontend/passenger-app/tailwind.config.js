/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        base: "#0E1420",
        surface: "#161F32",
        surface2: "#1E2A42",
        border: "#2A3652",
        muted: "#8FA0C2",
        ink: "#F1F5FB",
        brand: "#22C58B",      // TNSTC-adjacent green, distinct from admin's signal teal
        brandDark: "#189A6C",
        saffron: "#F2A65A",     // warm accent nodding to TN state colors, used sparingly
        alert: "#F0576A",
      },
      fontFamily: {
        display: ["'Poppins'", "sans-serif"],
        body: ["'Inter'", "sans-serif"],
        mono: ["'JetBrains Mono'", "monospace"],
      },
      backdropBlur: { xs: '2px' },
    },
  },
  plugins: [],
}
