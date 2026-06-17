import { CheckCircle, XCircle, X } from 'lucide-react';
import './Toast.css';

function Toast({ toast, onClose }) {
  if (!toast) return null;

  const icono = toast.type === 'error'
    ? <XCircle size={20} aria-hidden="true" />
    : <CheckCircle size={20} aria-hidden="true" />;

  return (
    <div className={`toast toast--${toast.type}`} role="alert" aria-live="assertive">
      <span className="toast__icon">{icono}</span>
      <span className="toast__message">{toast.message}</span>
      <button className="toast__close" onClick={onClose} aria-label="Cerrar notificación">
        <X size={16} />
      </button>
    </div>
  );
}

export default Toast;
