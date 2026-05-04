

const MOCK_USERS = [
  {
    id: 1,
    rut: '12345678-9',
    email: 'admin@cbo.cl',
    password: 'admin123',
    nombres: 'Carlos',
    apellidos: 'Méndez',
    rol: 'ADMIN',
  },
  {
    id: 2,
    rut: '23456789-0',
    email: 'profesor@cbo.cl',
    password: 'profe123',
    nombres: 'María',
    apellidos: 'Fernández',
    rol: 'DOCENTE',
  },
  {
    id: 3,
    rut: '34567890-1',
    email: 'estudiante@cbo.cl',
    password: 'estudiante123',
    nombres: 'João',
    apellidos: 'Silva',
    rol: 'ESTUDIANTE',
  },
  {
    id: 4,
    rut: '45678901-2',
    email: 'apoderado@cbo.cl',
    password: 'apoderado123',
    nombres: 'Patricio',
    apellidos: 'González',
    rol: 'APODERADO',
  },
];

const generateToken = (user) => {
  return btoa(JSON.stringify({ userId: user.id, rol: user.rol, exp: Date.now() + 86400000 }));
};

export const login = async (email, password) => {
  const user = MOCK_USERS.find((u) => u.email === email && u.password === password);

  if (!user) {
    throw new Error('Credenciales inválidas. Verifica tu correo y contraseña.');
  }

  const token = generateToken(user);
  const { password: _, ...userWithoutPassword } = user;

  return { user: userWithoutPassword, token };
};

export const getCurrentUser = async () => {
  const storedUser = localStorage.getItem('user');
  if (!storedUser) {
    throw new Error('No hay sesión activa');
  }
  return JSON.parse(storedUser);
};

export const refreshToken = async () => {
  const user = await getCurrentUser();
  const token = generateToken(user);
  localStorage.setItem('token', token);
  return token;
};

export const logout = async () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

export default { login, getCurrentUser, refreshToken, logout };