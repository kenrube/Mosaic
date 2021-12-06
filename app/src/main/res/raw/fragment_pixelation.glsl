varying highp vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform highp float imageWidthFactor;
uniform highp float imageHeightFactor;
uniform highp float pixel;

void main()
{
    highp vec2 xy  = textureCoordinate.xy;
    highp float dx = pixel * imageWidthFactor;
    highp float dy = pixel * imageHeightFactor;
    highp vec2 textureCoordinateToUse = vec2(dx * floor(xy.x / dx), dy * floor(xy.y / dy));

    gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse);
}
