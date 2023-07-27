const express = require('express');
const jwt = require('jsonwebtoken');
const handlebars = require('express-handlebars');
const Client = require('node-radius-client');
const session = require('express-session');
const path = require('path');
const {dictionaries} = require('node-radius-utils');
const bodyParser = require('body-parser');
const Keycloak = require('keycloak-connect');
const fs = require("fs");

const app = express();
const memoryStore = new session.MemoryStore();
app.use(session({
    secret: 'mySecret',
    resave: false,
    saveUninitialized: true,
    store: memoryStore,
}));

const keycloak = new Keycloak({
    store: memoryStore,
});

app.use(keycloak.middleware());

app.use(bodyParser.urlencoded({extended: true}));

app.engine('handlebars', handlebars.engine());
app.set('view engine', 'handlebars');
app.set('main', './views');

app.set('view engine', '.hbs');

app.set('views', path.join(__dirname, 'views'));

function renderUI(request, response, status) {
    response.render('home', {
        status,
    })
}

const radiusClient = new Client({
    host: 'localhost',
    retries:1,
    dictionaries: [
        dictionaries.rfc2865.file,
        dictionaries.mikrotik.file
    ],
});

function readKeycloakJSON(){
    return JSON.parse(fs.readFileSync('keycloak.json','UTF-8'));
}


app.post('/serviceAccount', keycloak.protect(), async (request, response) => {
    const keycloakJson = readKeycloakJSON();
    radiusClient.accessRequest({
        secret: request.body.secret,
        attributes: [
            [dictionaries.rfc2865.attributes.USER_NAME, `${keycloakJson.resource}@${keycloakJson.realm}`],
            [dictionaries.rfc2865.attributes.USER_PASSWORD, keycloakJson.credentials.secret]
            // [],
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

app.listen(3001);

