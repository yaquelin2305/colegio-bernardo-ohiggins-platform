import { TrendingUp, CalendarCheck, UserCircle, MessageSquare } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import '../styles/VisualizadorNotasPage.css';

const alumno = null;

const asignaturas = [];

const observaciones = '';

function clasificarNota(nota) {
  if (nota >= 6.0) return 'celda-nota--alta';
  if (nota >= 4.0) return 'celda-nota--media';
  return 'celda-nota--baja';
}

function VisualizadorNotasPage() {
  return (
    <MainLayout titulo="Mi Boletín de Notas">
      <div className="visualizador-notas">

        {/* ── Encabezado alumno ── */}
        {alumno && (
          <section className="boletin__encabezado" aria-label="Datos del alumno">
            <div className="boletin__alumno-info">
              <div className="boletin__avatar">
                <UserCircle size={48} aria-hidden="true" />
              </div>
              <div>
                <h2 className="boletin__nombre">{alumno.nombre}</h2>
                <p className="boletin__meta">
                  <span>{alumno.rut}</span>
                  <span className="boletin__separador" aria-hidden="true">·</span>
                  <span>{alumno.curso}</span>
                  <span className="boletin__separador" aria-hidden="true">·</span>
                  <span>{alumno.periodo}</span>
                </p>
              </div>
            </div>

            <div className="boletin__stats">
              <article className="boletin__stat-card boletin__stat-card--accent" aria-label="Promedio general">
                <div className="boletin__stat-icono boletin__stat-icono--accent">
                  <TrendingUp size={20} aria-hidden="true" />
                </div>
                <span className="boletin__stat-label">Promedio General</span>
                <span className="boletin__stat-valor">{alumno.promedioGeneral.toFixed(1)}</span>
                <span className="boletin__stat-detalle">Sobre 7.0 máximo</span>
              </article>

              <article className="boletin__stat-card boletin__stat-card--primary" aria-label="Porcentaje de asistencia">
                <div className="boletin__stat-icono boletin__stat-icono--primary">
                  <CalendarCheck size={20} aria-hidden="true" />
                </div>
                <span className="boletin__stat-label">% Asistencia Total</span>
                <span className="boletin__stat-valor">{alumno.asistencia}%</span>
                <span className="boletin__stat-detalle">Mínimo requerido: 85%</span>
              </article>
            </div>
          </section>
        )}

        {/* ── Tabla de asignaturas ── */}
        <section className="boletin__tabla-wrapper" aria-label="Calificaciones por asignatura">
          <h3 className="boletin__seccion-titulo">Calificaciones por Asignatura</h3>
          <table className="boletin__tabla">
            <thead>
              <tr>
                <th scope="col">Asignatura</th>
                <th scope="col">Nota 1</th>
                <th scope="col">Nota 2</th>
                <th scope="col">Nota 3</th>
                <th scope="col">Promedio</th>
                <th scope="col">Estado</th>
              </tr>
            </thead>
            <tbody>
              {asignaturas.map(asig => (
                <tr key={asig.id}>
                  <td className="boletin__asignatura">{asig.nombre}</td>
                  {[asig.nota1, asig.nota2, asig.nota3].map((nota, i) => (
                    <td key={i} className={`boletin__celda-nota ${clasificarNota(nota)}`}>
                      {nota.toFixed(1)}
                    </td>
                  ))}
                  <td className={`boletin__celda-nota boletin__promedio ${clasificarNota(asig.promedio)}`}>
                    {asig.promedio.toFixed(1)}
                  </td>
                  <td>
                    <span className={`boletin__estado ${asig.promedio >= 4.0 ? 'boletin__estado--aprobado' : 'boletin__estado--reprobado'}`}>
                      {asig.promedio >= 4.0 ? 'Aprobado' : 'Reprobado'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* ── Observaciones ── */}
        <section className="boletin__observaciones" aria-label="Comentarios del profesor jefe">
          <h3 className="boletin__seccion-titulo">
            <MessageSquare size={17} aria-hidden="true" />
            Comentarios del Profesor Jefe
          </h3>
          <p className="boletin__observaciones-texto">{observaciones}</p>
        </section>

      </div>
    </MainLayout>
  );
}

export default VisualizadorNotasPage;
