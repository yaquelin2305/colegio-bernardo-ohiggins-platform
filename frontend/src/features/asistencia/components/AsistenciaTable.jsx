import { CheckCircle, XCircle, Clock3, Pencil } from 'lucide-react';
import '../styles/AsistenciaTable.css';

const ESTADOS = {
  presente: { label: 'Presente', icon: CheckCircle, class: 'asistencia-table__estado--presente' },
  ausente: { label: 'Ausente', icon: XCircle, class: 'asistencia-table__estado--ausente' },
  atrasado: { label: 'Atrasado', icon: Clock3, class: 'asistencia-table__estado--atrasado' },
};

const MOCK_ESTUDIANTES = [
  { id: 1, nombre: 'Alejandro Torres Vásquez', curso: '1° Básico A', estado: 'presente', hora: '08:05' },
  { id: 2, nombre: 'Belén Andrews Catalán', curso: '1° Básico A', estado: 'presente', hora: '08:02' },
  { id: 3, nombre: 'Camila Constanza Molina', curso: '1° Básico A', estado: 'presente', hora: '08:10' },
  { id: 4, nombre: 'Diego Hernán López', curso: '1° Básico A', estado: 'presente', hora: '08:00' },
  { id: 5, nombre: 'Emilia Rose Martínez', curso: '1° Básico A', estado: 'presente', hora: '08:08' },
  { id: 6, nombre: 'Fabián Alberto Reyes', curso: '1° Básico A', estado: 'atrasado', hora: '08:25' },
  { id: 7, nombre: 'Grace Ivette Sánchez', curso: '1° Básico A', estado: 'presente', hora: '08:03' },
  { id: 8, name: 'HugoExequiel González', curso: '1° Básico A', estado: 'presente', hora: '08:01' },
  { id: 9, nombre: 'Isidora Valentina Peña', curso: '1° Básico A', estado: 'ausente', hora: '-' },
  { id: 10, nombre: 'Javier Ignacio Soto', curso: '1° Básico A', estado: 'presente', hora: '08:06' },
  { id: 11, nombre: 'Karen Abigail Torres', curso: '1° Básico A', estado: 'presente', hora: '08:04' },
  { id: 12, nombre: 'Luis Felipe Vargas', curso: '1° Básico A', estado: 'presente', hora: '08:07' },
  { id: 13, nombre: 'María José Zepeda', curso: '1° Básico A', estado: 'ausente', hora: '-' },
  { id: 14, nombre: 'Nicolás David Arias', curso: '1° Básico A', estado: 'presente', hora: '08:02' },
  { id: 15, nombre: 'Olivia Camila Rivera', curso: '1° Básico A', estado: 'presente', hora: '08:09' },
  { id: 16, nombre: 'Pablo Andrés Lara', curso: '1° Básico A', estado: 'presente', hora: '08:00' },
  { id: 17, nombre: 'Quintín Exequiel Meza', curso: '1° Básico A', estado: 'atrasado', hora: '08:30' },
  { id: 18, nombre: 'Romina Paola Navarro', curso: '1° Básico A', estado: 'presente', hora: '08:05' },
  { id: 19, nombre: 'Samuel Isaac Ortega', curso: '1° Básico A', estado: 'presente', hora: '08:03' },
  { id: 20, nombre: 'Tamara Rebeca Paredes', curso: '1° Básico A', estado: 'ausente', hora: '-' },
  { id: 21, nombre: 'Unsigned Urrutia', curso: '1° Básico A', estado: 'presente', hora: '08:01' },
  { id: 22, nombre: 'Valeria Ximena Vega', curso: '1° Básico A', estado: 'presente', hora: '08:04' },
  { id: 23, nombre: 'Wilson Yáñez Zamora', curso: '1° Básico A', estado: 'presente', hora: '08:06' },
  { id: 24, nombre: 'Ximena Abigail Alarcón', curso: '1° Básico A', estado: 'presente', hora: '08:02' },
  { id: 25, nombre: 'Yasna Patricia Bermúdez', curso: '1° Básico A', estado: 'presente', hora: '08:08' },
  { id: 26, nombre: 'Zacarías Hernán Campos', curso: '1° Básico A', estado: 'presente', hora: '08:00' },
  { id: 27, nombre: 'Andrea Cristina Díaz', curso: '1° Básico A', estado: 'presente', hora: '08:05' },
  { id: 28, nombre: 'Bernardo Eloy Encina', curso: '1° Básico A', estado: 'presente', hora: '08:03' },
  { id: 29, nombre: 'Carla Fernanda Fuentes', curso: '1° Básico A', estado: 'ausente', hora: '-' },
  { id: 30, nombre: 'David Eduardo Gajardo', curso: '1° Básico A', estado: 'presente', hora: '08:07' },
  { id: 31, nombre: 'Erina Fabiola Hernández', curso: '1° Básico A', estado: 'presente', hora: '08:04' },
  { id: 32, nombre: 'Fabián Isaías Idi Burgos', curso: '1° Básico A', estado: 'presente', hora: '08:01' },
];

function AsistenciaTable() {
  const renderEstado = (estado) => {
    const config = ESTADOS[estado];
    const Icon = config.icon;
    return (
      <span className={`asistencia-table__estado ${config.class}`}>
        <Icon size={14} />
        {config.label}
      </span>
    );
  };

  const handleEditar = (id) => {
    console.log('Editar asistencia del estudiante:', id);
  };

  if (!MOCK_ESTUDIANTES.length) {
    return (
      <div className="asistencia-table-container">
        <p className="asistencia-table__empty">No hay registros de asistencia para los filtros seleccionados.</p>
      </div>
    );
  }

  return (
    <div className="asistencia-table-container">
      <table className="asistencia-table" role="table" aria-label="Lista de asistencia">
        <thead>
          <tr>
            <th scope="col">N°</th>
            <th scope="col">Nombre Estudiante</th>
            <th scope="col">Curso</th>
            <th scope="col">Estado</th>
            <th scope="col">Hora</th>
            <th scope="col">Acción</th>
          </tr>
        </thead>
        <tbody>
          {MOCK_ESTUDIANTES.map((estudiante, index) => (
            <tr key={estudiante.id}>
              <td className="asistencia-table__numero">{index + 1}</td>
              <td className="asistencia-table__nombre">{estudiante.nombre}</td>
              <td>
                <span className="asistencia-table__curso">{estudiante.curso}</span>
              </td>
              <td>{renderEstado(estudiante.estado)}</td>
              <td className="asistencia-table__hora">{estudiante.hora}</td>
              <td className="asistencia-table__action">
                <button
                  className="asistencia-table__btn"
                  onClick={() => handleEditar(estudiante.id)}
                  aria-label={`Editar asistencia de ${estudiante.nombre}`}
                >
                  <Pencil size={16} />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AsistenciaTable;