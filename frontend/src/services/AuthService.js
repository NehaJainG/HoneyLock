import axios from 'axios';

const API_URL = 'http://localhost:8080/api/user'; // Your backend API URL

const AuthService = {
  register: async (name, username, password) => {
    try {
      const response = await axios.post("http://localhost:8080/api/user/register", {
        name,
        username,
        password
      },{
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log('Registration successful:', response.data);
    } catch (error) {
      console.error('Registration failed:', error);
    }
  },

  login: async (username, password) => {
    try {
      const response = await axios.post(`${API_URL}/login`, {
        username,
        password
      },{ 
        withCredentials: true,
      });
      console.log('Authentication response:', response.data);
    } catch (error) {
      console.error('Authentication failed:', error);
    }
  },

};

export default AuthService;
