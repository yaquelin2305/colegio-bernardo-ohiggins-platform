import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import RegisterPage from "./features/auth/pages/RegisterPage";
import DashboardPage from "./features/gestion-academica/pages/DashboardPage";
import RegistroNotasPage from "./features/gestion-academica/pages/RegistroNotasPage";
import VisualizadorNotasPage from "./features/gestion-academica/pages/VisualizadorNotasPage";
import BandejaMensajesPage from "./features/comunicaciones/pages/BandejaMensajesPage";
import RedactarMensajePage from "./features/comunicaciones/pages/RedactarMensajePage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/registro" replace />} />
        <Route path="/registro" element={<RegisterPage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/calificaciones" element={<RegistroNotasPage />} />
        <Route path="/mis-calificaciones" element={<VisualizadorNotasPage />} />
        <Route path="/comunicaciones" element={<BandejaMensajesPage />} />
        <Route
          path="/comunicaciones/redactar"
          element={<RedactarMensajePage />}
        />
      </Routes>
    </Router>
  );
}

export default App;
