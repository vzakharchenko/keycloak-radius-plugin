// index.js
const path = require('path');
const express = require('express');
const engine = require('express-handlebars');
const Keycloak = require('keycloak-connect');
var session = require('express-session');
const Client = require('node-radius-client');
const {dictionaries} = require('node-radius-utils');
const bodyParser = require('body-parser');

const memoryStore = new session.MemoryStore();

const keycloak = new Keycloak({
    store: memoryStore
});

const app = express();

app.use(bodyParser.urlencoded({extended: true}));
app.use(session({
    secret: 'mySecret',
    resave: false,
    saveUninitialized: true,
    store: memoryStore
}));

app.use(keycloak.middleware({
    logout: '/logout',
}));

app.engine('handlebars', engine());
app.set('view engine', 'handlebars');
app.set('main', './views');

app.set('view engine', '.hbs');

app.set('views', path.join(__dirname, 'views'));

function tokenInfo(accessToken) {
    const oneTimePassword = accessToken[accessToken.np];
    const userName = accessToken[accessToken.n];
    const realm = getRealmName(accessToken.iss);
    return {
        oneTimePassword,
        userName,
        realm,
        exp: accessToken.exp,
    }
}

function getRealmName(url) {
    const n = url.lastIndexOf('/');
    return url.substring(n + 1);
}

function renderUI(request, response, status) {
    const accessToken = request.kauth.grant.access_token.content;
    const {oneTimePassword, userName, exp, realm} = tokenInfo(accessToken);
    response.render('home', {
        name: userName,
        password: oneTimePassword,
        status,
        realm,
        exp: new Date(1000 * exp).toISOString()
    })
}


const radiusClient = new Client({
    host: 'localhost',
    dictionaries: [
        dictionaries.rfc2865.file,
        dictionaries.mikrotik.file
    ],
});

app.post('/', (request, response) => {
    radiusClient.accessRequest({
        secret: request.body.secret,
        attributes: [
            [dictionaries.rfc2865.attributes.USER_NAME, request.body.userName],
            [dictionaries.rfc2865.attributes.USER_PASSWORD, request.body.oneTimePassword],
            // [],
            ['Vendor-Specific', 14988,
                [[dictionaries.mikrotik.attributes.MIKROTIK_REALM, Buffer.from(request.body.realm)]]],
        ],
    }).then((result) => {
        console.log('result', result.code);
        renderUI(request, response, 'SUCCESS');

    }).catch((error) => {
        console.log('error', error);
        renderUI(request, response,
            error.response && error.response.code === 'Access-Reject' ? 'REJECT' : error);
    });

});


app.get('/', keycloak.protect(), (request, response) => {
    renderUI(request, response, "<<==");
});

app.listen(3000, ()=>{
    console.log(`http://localhost:3000`)
});

