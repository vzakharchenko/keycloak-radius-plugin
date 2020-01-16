import jwt from 'jsonwebtoken';
import { KeycloakPublicKeyFetcher } from './keyCloakCerts';
import { fetchData } from './restCalls';

function getRealmName(url) {
  const n = url.lastIndexOf('/');
  return url.substring(n + 1);
}

function getRealmNameFromToken(payloadjwt) {
  return getRealmName(payloadjwt.iss);
}

export async function verifyToken(token, keycloakJSON) {
  const decodedJwt = jwt.decode(token, { complete: true });
  if (!decodedJwt || !decodedJwt.header) {
    throw new Error('invalid token (header part)');
  } else {
    const { kid } = decodedJwt.header;
    const { alg } = decodedJwt.header;
    const realm = getRealmNameFromToken(decodedJwt.payload);
    if (alg.toLowerCase() !== 'none' && !alg.toLowerCase().startsWith('hs') && kid) {
      // fetch the PEM Public Key

      try {
        const publicKeyFunc = KeycloakPublicKeyFetcher(keycloakJSON['auth-server-url'],
          realm,
          kid);
        const key = await publicKeyFunc;
        return jwt.verify(token, key);
      } catch (e) {
        // Token is not valid
        throw new Error(`invalid token: ${e}`);
      }
    } else {
      throw new Error('invalid token');
    }
  }
}

export function getPassword(decodedToken) {
  return decodedToken[decodedToken.np || 'p'];
}

export function getUserName(decodedToken) {
  return decodedToken[decodedToken.n];
}

export function loadKeycloakJson() {
  return new Promise((resolve, reject) => {
    fetchData('/keycloak.json').then((r) => {
      resolve(JSON.parse(r.data));
    }).catch((e) => {
      if (e.response && e.response.status === 404) {
        // eslint-disable-next-line prefer-promise-reject-errors
        reject('Cannot found /keycloak.json');
      } else {
        reject(e.response ? e.response.data : e);
      }
    });
  });
}
