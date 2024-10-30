import axios from 'axios';
const AuthService = {
    register: async (user) => {
      try {
        const { name, username, password } = user
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
  
    login: async (user) => {

      try {
        const { username, password } = user
        console.log("starts: ",user);
        const response = await axios.post("http://localhost:8080/api/user/login", {
            username,
            password
          },{
            withCredentials: true,
            headers: {
              "Content-Type": "application/json",
            },
          });
        console.log('Authentication response:', response.data);
      } catch (error) {
        console.error('Authentication failed:', error);
      }
    },
  
};

export default AuthService;