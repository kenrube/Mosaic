varying highp vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float subtrahend;

void main()
{
    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    gl_FragColor = vec4(abs(tan(textureColor.rgb - subtrahend)), textureColor.a);
}
