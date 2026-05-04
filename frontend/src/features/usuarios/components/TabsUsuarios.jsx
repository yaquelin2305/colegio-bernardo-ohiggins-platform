const TABS = [
  { key: 'docentes',    label: 'Docentes' },
  { key: 'apoderados',  label: 'Apoderados' },
  { key: 'estudiantes', label: 'Estudiantes' },
];

function TabsUsuarios({ tabActiva, onCambiarTab }) {
  return (
    <div className="gestion-usuarios__tabs" role="tablist" aria-label="Tipo de usuario">
      {TABS.map(({ key, label }) => (
        <button
          key={key}
          role="tab"
          aria-selected={tabActiva === key}
          className={`gestion-usuarios__tab ${tabActiva === key ? 'gestion-usuarios__tab--activa' : ''}`}
          onClick={() => onCambiarTab(key)}
        >
          {label}
        </button>
      ))}
    </div>
  );
}

export default TabsUsuarios;
