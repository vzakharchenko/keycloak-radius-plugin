const webpack = require('webpack');
const HtmlWebPackPlugin = require('html-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const path = require('path');

const progressBarPlugin = new ProgressBarPlugin();

const env = process.env.NODE_ENV || 'development';


const copyConfigs = [];

if (env === 'production') {
    copyConfigs.push({
        from: './prod/login.html',
        to: 'login.html',
    });
} else {
    copyConfigs.push({
        from: './dev/keycloak.json',
        to: 'keycloak.json',
    });
    copyConfigs.push({
        from: './dev/indexDev.js',
        to: 'index.js',
    });
    copyConfigs.push({
        from: './md5.js',
        to: 'md5.js',
    });
}

const plugins = [
    new webpack.optimize.OccurrenceOrderPlugin(true),
    new CopyWebpackPlugin(copyConfigs),
    //    copyConfig,
    progressBarPlugin,
];

if (env !== 'production') {
    const htmlPlugin = new HtmlWebPackPlugin({
        template: './dev/login.html',
        filename: './login.html',
    });
    plugins.push(htmlPlugin);
}

const optimization = {};
if (env === 'production') {
    optimization.minimize = true;
    optimization.namedModules = false;
    optimization.namedChunks = false;
    optimization.mangleWasmImports = true;
    optimization.moduleIds = 'hashed';
    optimization.minimizer = [new TerserPlugin()];
}

module.exports = {
    output: {
        path: path.resolve(__dirname, '..', 'mikrotik'),
        filename: 'authorization.js',
    },
    devServer: {
        contentBase: 'public',
        historyApiFallback: true,
        hot: false,
        inline: false,
        host: '0.0.0.0',
        disableHostCheck: true,
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                },
            },
            {
                test: /\.css$/,
                use: [
                    {
                        loader: 'style-loader',
                    },
                    {
                        loader: 'css-loader',
                        options: {
                            modules: true,
                            importLoaders: 1,
                        },
                    },
                ],
            },
        ],
    },
    plugins,
    optimization,
};
