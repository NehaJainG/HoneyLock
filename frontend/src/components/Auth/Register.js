import React, { useState } from 'react';
import AuthService from '../../services/AuthService';
import '../styles/AuthStyles.css';  // Import the CSS

const Register = () => {
    const [username, setUsername] = useState('');
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);

    const handleRegister = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            const response = await AuthService.register({username, name, password });
            console.log('Registration successful:', response);
            setError(response.data);
        } catch (error) {
            setError(error.response ? error.response.data.message : "Registration failure");
            console.error('Registration failed:', error);
        }
    };

    return (
        <form onSubmit={handleRegister}>
            <div>
                <label>Username</label>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <div>
                <label>Name</label>
                <input
                    type="text"
                    placeholder="Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
            </div>
            <div>
                <label>Password</label>
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            <button type="submit">Register</button>
            {error && <p>{error}</p>}  {/* Display error message if exists */}
        </form>
    );
};

export default Register;
