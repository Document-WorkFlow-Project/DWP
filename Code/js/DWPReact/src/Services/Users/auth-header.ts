export default function authHeader() {

  const userStr = localStorage.getItem("user");
  let user = null;
  if (userStr)
    user = JSON.parse(userStr);

  //let headers = {Cookie: ''};

  if (user && user.token) {

    //console.log({ Authorization: 'Bearer ' + user.token })
    // return { Authorization: 'Bearer ' + user.token }; // for Spring Boot back-end
    // return { 'x-access-token': user.accessToken };       // for Node.js Express back-end

    //document.cookie = `token=${user.token}`;
    // Add the cookie to the headers
   // return {Cookie:`token=${user.token}`}
  }
  return {}
  //return {Cookie: ''}
}
