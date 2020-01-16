import Keycloak from 'keycloak-js';
import {
  getPassword, getUserName, loadKeycloakJson, verifyToken,
} from './base/loginUtils';


function setError(message) {
  const errorElement = document.getElementById('error');
  errorElement.innerHTML = `<br /><div style="color: #FF8080; font-size: 9px">${message}</div>`;
}

function clear() {
  const rootElement = document.getElementById('root');
  rootElement.innerHTML = 'Please wait...';
}
if (!error) {
  loadKeycloakJson()
    .then((json) => {
      const keycloak = new Keycloak('/keycloak.json');
      keycloak.init({
        onLoad: 'login-required',
        promiseType: 'native',
      }).then((authenticated) => {
        if (authenticated) {
          verifyToken(keycloak.token, json).then((decodedToken) => {
            const password = getPassword(decodedToken);
            const userName = getUserName(decodedToken);
            if (!userName || !password) {
              setError(`Client ${json.resource} is not configured. Please check client mapper.`);
            } else {
              doLogin(userName, password);
              clear();
            }
          }).catch((ve) => {
            setError(`failed to verify token, please check configuration: ${ve}`);
          });
        } else {
          setError('failed to initialize SSO, please check configuration');
        }
      }).catch((e) => {
        setError(`failed to initialize SSO, please check configuration: ${e}`);
      });
    })
    .catch((e) => {
      setError(`failed to load keycloak.json, please check configuration: ${e}`);
    });
} else {
  setError(`${error}`);
}
