function TarjetaKpi({ label, numero, detalle, variante, icono }) {
  return (
    <article className={`stat-card${variante !== 'primary' ? ` stat-card--${variante}` : ''}`}>
      <div className={`stat-card__icono-wrapper stat-card__icono-wrapper--${variante}`}>
        {icono}
      </div>
      <span className="stat-card__label">{label}</span>
      <span className="stat-card__numero">{numero}</span>
      <span className="stat-card__detalle">{detalle}</span>
    </article>
  );
}

export default TarjetaKpi;
