varying highp vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float threshold;

// Values from "Graphics Shaders: Theory and Practice" by Bailey and Cunningham
const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);

void main()
{
    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);
    lowp float thresholdResult = step(luminance, threshold);
    lowp vec3 finalColor = abs(thresholdResult - textureColor.rgb);
    
    gl_FragColor = vec4(finalColor, textureColor.a);
}
