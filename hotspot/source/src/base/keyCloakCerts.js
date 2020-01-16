import { fetchData } from './restCalls';

const BEGIN_KEY = '-----BEGIN RSA PUBLIC KEY-----\n';
const END_KEY = '\n-----END RSA PUBLIC KEY-----\n';

function parse(response) {
  return new Promise((resolve, reject) => {
    try {
      const parsedData = JSON.parse(response);
      resolve(parsedData);
    } catch (e) {
      reject(e);
    }
  });
}

function getJson(url) {
  return new Promise((resolve, reject) => {
    fetchData(url, 'GET').then((res) => {
      parse(res.data)
        .then(result => resolve(result))
        .catch(error => reject(error));
    }).catch((e) => {
      reject(new Error(`Status: ${e.statusCode}`));
    });
  });
}

function getKey(response, kid) {
  return Object.hasOwnProperty.call(response, 'keys')
    ? response.keys.find(k => k.kid === kid)
    : undefined;
}

async function getKeyFromKeycloak(url, kid) {
  const response = await getJson(url);
  const key = getKey(response, kid);
  if (!key) {
    throw new Error(`Can't find key for kid "${kid}" in response.`);
  }
  return key;
}

function verify(key) {
  if (!(key.n && key.e)) {
    throw new Error('Can\'t find modulus or exponent in key.');
  }
  if (key.kty !== 'RSA') {
    throw new Error('Key type (kty) must be RSA.');
  }
  if (key.alg !== 'RS256') {
    throw new Error('Algorithm (alg) must be RS256.');
  }
}

// Based on tracker1's node-rsa-pem-from-mod-exp module.
// See https://github.com/tracker1/node-rsa-pem-from-mod-exp

function convertToHex(str) {
  const hex = Buffer.from(str, 'base64').toString('hex');
  return hex[0] < '0' || hex[0] > '7'
    ? `00${hex}`
    : hex;
}

function toHex(number) {
  const str = number.toString(16);
  return (str.length % 2)
    ? `0${str}`
    : str;
}

function toLongHex(number) {
  const str = toHex(number);
  const lengthByteLength = 128 + (str.length / 2);
  return toHex(lengthByteLength) + str;
}

function encodeLenght(n) {
  return n <= 127
    ? toHex(n)
    : toLongHex(n);
}

function getPublicKey(modulus, exponent) {
  const mod = convertToHex(modulus);
  const exp = convertToHex(exponent);
  const encModLen = encodeLenght(mod.length / 2);
  const encExpLen = encodeLenght(exp.length / 2);
  const part = [mod, exp, encModLen, encExpLen].map(n => n.length / 2).reduce((a, b) => a + b);
  const bufferSource = `30${encodeLenght(part + 2)}02${encModLen}${mod}02${encExpLen}${exp}`;
  const pubkey = Buffer.from(bufferSource, 'hex').toString('base64');
  return BEGIN_KEY + pubkey.match(/.{1,64}/g).join('\n') + END_KEY;
}
async function fetch(url, kid) {
  const key = await getKeyFromKeycloak(url, kid);

  verify(key);
  return getPublicKey(key.n, key.e);
}

// eslint-disable-next-line import/prefer-default-export
export async function KeycloakPublicKeyFetcher(url, realm, kid) {
  const certsUrl = realm ? `${url}/realms/${realm}/protocol/openid-connect/certs` : url;
  // eslint-disable-next-line no-return-await
  return await fetch(certsUrl, kid);
}
