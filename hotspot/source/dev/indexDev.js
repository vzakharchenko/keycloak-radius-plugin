/* eslint-disable no-unused-vars,no-var,vars-on-top */
// Common server variables
var hostname = '$(hostname)';
var identity = '$(identity)';
var serverAddress = '$(server-address)';
var sslLogin = '$(ssl-login)';
var serverName = '$(server-name)';

// Links
var linkLogin = '$(link-login)';
var loginOnly = '$(link-login-only)';
var linkLogout = '$(link-logout)';
var linkStatus = '$(link-status)';
var linkOrig = '$(link-orig)';

var chapId = '$(chap-id)';
var chapChallenge = '$(chap-challenge)';
var error = '';
var trial = '$(trial)';

// eslint-disable-next-line no-unused-vars
function doLogin(username, password) {
  document.sendin.action = loginOnly;
  document.sendin.username.value = username;
  document.sendin.dst.value = linkOrig;
  var psw = password;
  if (chapId) {
    psw = hexMD5(chapId + password + chapChallenge);
  }
  document.sendin.password.value = psw;
  document.sendin.submit();
  return false;
}
