import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const ProtectedRoute = ({ children, requireAdmin = false }) => {
    const { user, loading } = useAuth();
    const location = useLocation();

    if (loading) {
        return null; // or a loading spinner
    }

    if (!user) {
        // Redirect to login page but save the attempted url
        return <Navigate to="/auth" state={{ from: location }} replace />;
    }

    if (requireAdmin && user.role !== 'ROLE_ADMIN') {
        // Redirect to home page if admin access is required but user is not an admin
        return <Navigate to="/" replace />;
    }

    return children;
};

export default ProtectedRoute; 