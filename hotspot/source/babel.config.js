module.exports = function (api) {
  api.cache(true);

  const presets = [
    '@babel/env',
  ];
  const plugins = [
    [
      '@babel/transform-runtime',
      {
        regenerator: true,
      },
    ],
    [
      '@babel/plugin-proposal-decorators',
      {
        legacy: true,
      },
    ],
  ];

  // if (process.env.NODE_ENV === 'test') {
  //   plugins.push('babel-plugin-dynamic-import-node');
  // } else {
  //   plugins.push('@babel/plugin-syntax-dynamic-import');
  // }

  return {
    presets,
    plugins,
  };
};
