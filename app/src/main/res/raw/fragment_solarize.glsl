varying highp vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform highp float threshold;

// Values from "Graphics Shaders: Theory and Practice" by Bailey and Cunningham
const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);

void main()
{
    highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    highp float luminance = dot(textureColor.rgb, luminanceWeighting);
    highp float thresholdResult = step(luminance, threshold);
    highp vec3 finalColor = abs(thresholdResult - textureColor.rgb);
    
    gl_FragColor = vec4(finalColor, textureColor.w);
}
