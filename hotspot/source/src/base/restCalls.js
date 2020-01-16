import fetch from 'axios';

function errorHandler(response) {
  console.debug('error:', response.data);
}

export function fetchData(url, method = 'GET', headers) {
  return new Promise((resolve, reject) => {
    fetch({
      url,
      method,
      headers,
      transformResponse: req => req,
      withCredentials: true,
    }).then((response) => {
      resolve(response);
    }).catch((response) => {
      errorHandler(response);
      reject(response);
    });
  });
}

export function sendData(url, method = 'POST', data, headers) {
  return new Promise((resolve, reject) => {
    fetch({
      url,
      method,
      data,
      transformResponse: req => req,
      headers,
      withCredentials: true,
    }).then((response) => {
      resolve(response);
    }).catch((response) => {
      errorHandler(response);
      reject(response);
    });
  });
}
