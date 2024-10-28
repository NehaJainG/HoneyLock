import React, { useState } from 'react';
import AuthService from '../../services/AuthService';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(null); // Clear any previous error

    try {
      const response = await AuthService.login({ username, password });
      console.log('Login successful:', response);
      // Handle successful login here (e.g., redirect or save token)
    } catch (error) {
      // If axios throws an error, it's often an object with several fields, so we should handle it accordingly
      if (error.response && error.response.data && error.response.data.message) {
        setError(error.response.data.message); // Set the specific error message from the response
      } else {
        console.log(error);
        setError('Login successful'); // Default error message
      }
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
        <div>
          <label>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit">Login</button>
        {/* Display error message if it exists */}
        {error && <p style={{ color: 'red' }}>{error}</p>}
      </form>
    </div>
  );
};

export default Login;
