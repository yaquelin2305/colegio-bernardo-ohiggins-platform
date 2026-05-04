function CuerpoMensaje({ cuerpo }) {
  return (
    <section className="detalle-mensaje__cuerpo-wrapper" aria-label="Contenido del mensaje">
      <pre className="detalle-mensaje__cuerpo">{cuerpo}</pre>
    </section>
  );
}

export default CuerpoMensaje;
