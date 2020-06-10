const express = require('express');
const exphbs = require('express-handlebars');
const Client = require('node-radius-client');
const path = require('path');
const {dictionaries} = require('node-radius-utils');
const bodyParser = require('body-parser');

const app = express();

app.use(bodyParser.urlencoded({extended: true}));

app.engine('.hbs', exphbs({
    defaultLayout: 'main',
    extname: '.hbs',
    layoutsDir: path.join(__dirname, 'views/layouts')
}));

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

app.post('/', (request, response) => {
    radiusClient.accessRequest({
        secret: request.body.secret,
        attributes: [
            [dictionaries.rfc2865.attributes.USER_NAME, request.body.userName],
            [dictionaries.rfc2865.attributes.USER_PASSWORD, request.body.password]
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


app.get('/', (request, response) => {
    renderUI(request, response, "<<==");
});

app.listen(3001);

