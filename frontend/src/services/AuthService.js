import axios from 'axios';
import { generateLargeRandomInteger } from './generateSecret';
const AuthService = {
    register: async (user) => {
      try {
        const secret = generateLargeRandomInteger(30);
        const userData = {
          name: user.name,
          username : user.username,
          cipher: user.password,
          secret : secret,
        }
        console.log(userData);
        const response = await axios.post("http://localhost:8080/api/user/register",
          userData ,{
          withCredentials: true,
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include"
        });
        console.log('Registration successful:', response.data);
        return response;
      } catch (error) {
        console.error('Registration failed:', error);
      }
    },
  
    login: async (user) => {

      try {
        //const { username, password } = user
        console.log("starts: ",user);
        const response = await axios.post("http://localhost:8080/api/user/login", {
            username : user.username,
            cipher : user.password
          },{
            withCredentials: true,
            headers: {
              "Content-Type": "application/json",
            },
          });
        console.log('Authentication response:', response.data);
        return response;
      } catch (error) {
        console.error('Authentication failed:', error);
      }
    },
  
};

export default AuthService;