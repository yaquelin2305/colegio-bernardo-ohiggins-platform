import { ClipboardCheck } from 'lucide-react';
import ResumenAsistencia from '../components/ResumenAsistencia';
import FiltroAsistencia from '../components/FiltroAsistencia';
import AsistenciaTable from '../components/AsistenciaTable';
import '../styles/AsistenciaPage.css';

function AsistenciaPage() {
  const handleFiltrar = (filtros) => {
    console.log('Filtrar:', filtros);
  };

  return (
    <main className="asistencia-page">
      <header className="asistencia-page__header">
        <div className="asistencia-page__title-container">
          <div className="asistencia-page__icon">
            <ClipboardCheck size={24} />
          </div>
          <div>
            <h1 className="asistencia-page__title">Gestión de Asistencia</h1>
            <p className="asistencia-page__subtitle">Control diario de asistencia por curso</p>
          </div>
        </div>
      </header>

      <section className="asistencia-page__content">
        <ResumenAsistencia />
        <FiltroAsistencia onFiltrar={handleFiltrar} />
        <AsistenciaTable />
      </section>
    </main>
  );
}

export default AsistenciaPage;