module.exports = {
    mode: "development",
    resolve: {
        extensions: [".js", ".ts", ".tsx"]
    },
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                //target: 'http://dwp-backend:8080', // IMPORTANT! use this target when building the image for docker
                pathRewrite: {'^/api': ''},
                secure: false
            }
        },
        historyApiFallback: { disableDotRule: true },
        port: 3000
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: [
                    'style-loader',
                    'css-loader',

                ]
            },
            {
                test: /\.scss$/,
                use: [
                    'style-loader',
                    'css-loader',
                    'sass-loader'
                ]
            },
            {
                test: /\.svg$/,
                use: "file-loader",
            },
            {
                test: /\.(jpe?g|png|gif|svg)$/i,
                loader: "url-loader"
            },
            {
                test: /\.(mov|mp4)$/,
                use: [
                    {
                        loader: 'file-loader',
                        options: {
                            name: '[name].[ext]'
                        }
                    }
                ]
            },
            {
                test: /\.m?js$/,
                enforce: 'pre',
                use: ['source-map-loader'],
            }
        ]
    }
}