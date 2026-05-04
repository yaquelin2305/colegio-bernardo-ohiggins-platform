function clasificarPromedio(promedio) {
  if (promedio >= 6.0) return 'nota--alta';
  if (promedio >= 4.0) return 'nota--media';
  return 'nota--baja';
}

function TablaCalificaciones({ alumnos, onNotaChange }) {
  return (
    <section className="registro-notas__tabla-wrapper" aria-label="Tabla de calificaciones">
      <table className="registro-notas__tabla">
        <thead>
          <tr>
            <th scope="col">RUT</th>
            <th scope="col">Nombre Alumno</th>
            <th scope="col">Nota 1</th>
            <th scope="col">Nota 2</th>
            <th scope="col">Nota 3</th>
            <th scope="col">Promedio</th>
          </tr>
        </thead>
        <tbody>
          {alumnos.map(alumno => (
            <tr key={alumno.id}>
              <td className="registro-notas__rut">{alumno.rut}</td>
              <td className="registro-notas__nombre">{alumno.nombre}</td>
              {['nota1', 'nota2', 'nota3'].map(campo => (
                <td key={campo} className="registro-notas__celda-nota">
                  <input
                    type="number"
                    className="registro-notas__input-nota"
                    min="1"
                    max="7"
                    step="0.1"
                    value={alumno[campo]}
                    aria-label={`${campo.replace('nota', 'Nota ')} de ${alumno.nombre}`}
                    onChange={e => onNotaChange(alumno.id, campo, e.target.value)}
                  />
                </td>
              ))}
              <td className={`registro-notas__promedio ${clasificarPromedio(alumno.promedio)}`}>
                {alumno.promedio.toFixed(1)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

export default TablaCalificaciones;
