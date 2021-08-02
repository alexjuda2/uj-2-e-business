const path = require('path');
const HtmlWebPackPlugin = require('html-webpack-plugin');

module.exports = env => {
  return {
    output: {
      path: path.resolve(__dirname, 'build', env.production ? 'prod' : 'dev'),
      filename: 'bundle.js',
    },
    resolve: {
      modules: [path.join(__dirname, 'src'), 'node_modules'],
      alias: {
        react: path.join(__dirname, 'node_modules', 'react'),
      },
    },
    module: {
      rules: [
        {
          test: /\.(js|jsx)$/,
          exclude: /node_modules/,
          use: {
            loader: 'babel-loader',
          },
        },
      ],
    },
    plugins: [
      new HtmlWebPackPlugin({
        template: './src/index.html',
      }),
    ],
    devtool: 'source-map',
  }
};
