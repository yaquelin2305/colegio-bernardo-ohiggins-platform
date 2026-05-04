function clasificarNota(nota) {
  if (nota >= 6.0) return 'celda-nota--alta';
  if (nota >= 4.0) return 'celda-nota--media';
  return 'celda-nota--baja';
}

function TablaBoletinNotas({ asignaturas }) {
  return (
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
  );
}

export default TablaBoletinNotas;
