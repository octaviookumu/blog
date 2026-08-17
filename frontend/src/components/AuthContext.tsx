import React, {
  createContext,
  useContext,
  useState,
  useCallback,
  useEffect,
} from 'react';
import * as authApi from '../api/auth';
import { AuthResponse } from '../api/types';

interface AuthUser {
  id: string;
  name: string;
  email: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: AuthUser | null;
  login: (authResponse: AuthResponse) => void;
  logout: () => void;
  token: string | null;
}

interface AuthProviderProps {
  children: React.ReactNode;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(
    localStorage.getItem('token'),
  );

  // Initialize auth state from token
  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken) {
        try {
          // TODO: Add endpoint to fetch user profile
          // const userProfile = await apiService.getUserProfile();
          // setUser(userProfile);
          setIsAuthenticated(true);
          setToken(storedToken);
        } catch (error) {
          // If token is invalid, clear authentication
          localStorage.removeItem('token');
          setIsAuthenticated(false);
          setUser(null);
          setToken(null);
        }
      }
    };

    initializeAuth();
  }, []);

  const login = useCallback((authResponse: AuthResponse) => {
    // The actual network request is performed by the useLogin() mutation;
    // this just applies the result to the shared auth state. authApi.login
    // already persisted the token to localStorage.
    setToken(authResponse.token);
    setIsAuthenticated(true);

    // TODO: Add endpoint to fetch user profile after login
    // const userProfile = await apiService.getUserProfile();
    // setUser(userProfile);
  }, []);

  const logout = useCallback(() => {
    authApi.logout(); // Clears the token from localStorage
    setIsAuthenticated(false);
    setUser(null);
    setToken(null);
  }, []);

  // Note: unlike the old axios setup, there's no "sync the token onto the
  // http client" step needed here - request() in api/client.ts reads
  // localStorage fresh on every call, so setting the token above is enough.

  const value = {
    isAuthenticated,
    user,
    login,
    logout,
    token,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
