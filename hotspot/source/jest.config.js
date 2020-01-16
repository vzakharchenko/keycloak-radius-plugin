module.exports = {
  setupFiles: ['./unitTestConfig/jestSetup.js'],
  transform: {
    '^.+\\.js$': 'babel-jest',
  },
  transformIgnorePatterns: ['<rootDir>/node_modules/'],
};
