package keystrokesmod.client.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

public class ShaderUtils implements IMinecraft {
    private final int programID;

    public ShaderUtils(String fragmentShaderLoc, String vertexShaderLoc) {
        int program = glCreateProgram();
        int fragmentShaderID;

		switch (fragmentShaderLoc) {
		    case "shadow":
		        fragmentShaderID = createShader(new ByteArrayInputStream(bloom.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "roundRectTexture":
		        fragmentShaderID = createShader(new ByteArrayInputStream(roundRectTexture.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "roundRectOutline":
		        fragmentShaderID = createShader(new ByteArrayInputStream(roundRectOutline.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "roundedRect":
		        fragmentShaderID = createShader(new ByteArrayInputStream(roundedRect.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "roundedRectGradient":
		        fragmentShaderID = createShader(new ByteArrayInputStream(roundedRectGradient.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "gradient":
		        fragmentShaderID = createShader(new ByteArrayInputStream(gradient.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "kawaseUp":
		        fragmentShaderID = createShader(new ByteArrayInputStream(kawaseUp.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "kawaseDown":
		        fragmentShaderID = createShader(new ByteArrayInputStream(kawaseDown.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "kawaseUpBloom":
		        fragmentShaderID = createShader(new ByteArrayInputStream(kawaseUpBloom.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "kawaseDownBloom":
		        fragmentShaderID = createShader(new ByteArrayInputStream(kawaseDownBloom.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "gaussianBlur":
		        fragmentShaderID = createShader(new ByteArrayInputStream(gaussianBlur.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "outline":
		        fragmentShaderID = createShader(new ByteArrayInputStream(outline.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    case "glow":
		        fragmentShaderID = createShader(new ByteArrayInputStream(glow.getBytes()), GL_FRAGMENT_SHADER);
		        break;
		    default:
		        try {
		            fragmentShaderID = createShader(
		                mc.getResourceManager()
		                  .getResource(new ResourceLocation(fragmentShaderLoc))
		                  .getInputStream(),
		                GL_FRAGMENT_SHADER
		            );
		        } catch (IOException e) {
		            throw new RuntimeException("Failed to load shader: " + fragmentShaderLoc, e);
		        }
		        break;
		}
		glAttachShader(program, fragmentShaderID);
		int vertexShaderID = createShader(new ByteArrayInputStream(vertex.getBytes()), GL_VERTEX_SHADER);
		glAttachShader(program, vertexShaderID);

        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.programID = program;
    }

    public ShaderUtils(String fragmentShaderLoc) {
        this(fragmentShaderLoc, "vertex.vsh");
    }


    public void init() {
        glUseProgram(programID);
    }

    public void unload() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(programID, name);
    }


    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1:
                glUniform1f(loc, args[0]);
                break;
            case 2:
                glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    public static void drawQuads(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }

    public static void drawQuads() {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    public static void drawQuads(float width, float height) {
        drawQuads(0.0f, 0.0f, width, height);
    }

    public static void drawFixedQuads() {
        ScaledResolution sr = new ScaledResolution(mc);
        drawQuads((float) (mc.displayWidth / sr.getScaleFactor()), (float) mc.displayHeight / sr.getScaleFactor());
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readInputStream(inputStream));
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    
    private final String vertex =
            "#version 120\n" +
            "\n" +
            "void main() {\n" +
            "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
            "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
            "}\n";

    private final String bloom =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform sampler2D inTexture, textureToCheck;\n" +
    	    "uniform vec2 texelSize, direction;\n" +
    	    "uniform float radius;\n" +
    	    "uniform float weights[256];\n" +
    	    "\n" +
    	    "#define offset texelSize * direction\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    if (direction.y > 0 && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;\n" +
    	    "    float blr = texture2D(inTexture, gl_TexCoord[0].st).a * weights[0];\n" +
    	    "\n" +
    	    "    for (float f = 1.0; f <= radius; f++) {\n" +
    	    "        blr += texture2D(inTexture, gl_TexCoord[0].st + f * offset).a * (weights[int(abs(f))]);\n" +
    	    "        blr += texture2D(inTexture, gl_TexCoord[0].st - f * offset).a * (weights[int(abs(f))]);\n" +
    	    "    }\n" +
    	    "\n" +
    	    "    gl_FragColor = vec4(0.0, 0.0, 0.0, blr);\n" +
    	    "}\n";

    private final String roundRectTexture =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform vec2 location, rectSize;\n" +
    	    "uniform sampler2D textureIn;\n" +
    	    "uniform float radius, alpha;\n" +
    	    "\n" +
    	    "float roundedBoxSDF(vec2 centerPos, vec2 size, float radius) {\n" +
    	    "    return length(max(abs(centerPos) - size, 0.)) - radius;\n" +
    	    "}\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    float distance = roundedBoxSDF((rectSize * .5) - (gl_TexCoord[0].st * rectSize), (rectSize * .5) - radius - 1., radius);\n" +
    	    "    float smoothedAlpha = (1.0 - smoothstep(0.0, 2.0, distance)) * alpha;\n" +
    	    "    gl_FragColor = vec4(texture2D(textureIn, gl_TexCoord[0].st).rgb, smoothedAlpha);\n" +
    	    "}\n";

    private final String roundRectOutline =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform vec2 location, rectSize;\n" +
    	    "uniform vec4 color, outlineColor;\n" +
    	    "uniform float radius, outlineThickness;\n" +
    	    "\n" +
    	    "float roundedSDF(vec2 centerPos, vec2 size, float radius) {\n" +
    	    "    return length(max(abs(centerPos) - size + radius, 0.0)) - radius;\n" +
    	    "}\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    float distance = roundedSDF(gl_FragCoord.xy - location - (rectSize * .5), (rectSize * .5) + (outlineThickness * .5) - 1.0, radius);\n" +
    	    "    float blendAmount = smoothstep(0.0, 2.0, abs(distance) - (outlineThickness * .5));\n" +
    	    "    vec4 insideColor = (distance < 0.0) ? color : vec4(outlineColor.rgb, 0.0);\n" +
    	    "    gl_FragColor = mix(outlineColor, insideColor, blendAmount);\n" +
    	    "}\n";

    private final String roundedRectGradient =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform vec2 location, rectSize;\n" +
    	    "uniform vec4 color1, color2, color3, color4;\n" +
    	    "uniform float radius;\n" +
    	    "\n" +
    	    "#define NOISE .5/255.0\n" +
    	    "\n" +
    	    "float roundSDF(vec2 p, vec2 b, float r) {\n" +
    	    "    return length(max(abs(p) - b , 0.0)) - r;\n" +
    	    "}\n" +
    	    "\n" +
    	    "vec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n" +
    	    "    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n" +
    	    "    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n" +
    	    "    return color;\n" +
    	    "}\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    vec2 st = gl_TexCoord[0].st;\n" +
    	    "    vec2 halfSize = rectSize * .5;\n" +
    	    "    float smoothedAlpha = (1.0 - smoothstep(0.0, 2.0, roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1.0, radius)));\n" +
    	    "    vec4 gradient = createGradient(st, color1, color2, color3, color4);\n" +
    	    "    gl_FragColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);\n" +
    	    "}\n";

    private final String roundedRect =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform vec2 location, rectSize;\n" +
    	    "uniform vec4 color;\n" +
    	    "uniform float radius;\n" +
    	    "uniform bool blur;\n" +
    	    "\n" +
    	    "float roundSDF(vec2 p, vec2 b, float r) {\n" +
    	    "    return length(max(abs(p) - b, 0.0)) - r;\n" +
    	    "}\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    vec2 rectHalf = rectSize * .5;\n" +
    	    "    float smoothedAlpha = (1.0 - smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1.0, radius))) * color.a;\n" +
    	    "    gl_FragColor = vec4(color.rgb, smoothedAlpha);\n" +
    	    "}\n";


    private final String kawaseUpBloom =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform sampler2D inTexture, textureToCheck;\n" +
    	    "uniform vec2 halfpixel, offset, iResolution;\n" +
    	    "uniform int check;\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    // if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a > 0.0) discard;\n" +
    	    "    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n" +
    	    "\n" +
    	    "    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n" +
    	    "    sum.rgb *= sum.a;\n" +
    	    "    vec4 smpl1 = texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);\n" +
    	    "    smpl1.rgb *= smpl1.a;\n" +
    	    "    sum += smpl1 * 2.0;\n" +
    	    "    vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n" +
    	    "    smp2.rgb *= smp2.a;\n" +
    	    "    sum += smp2;\n" +
    	    "    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);\n" +
    	    "    smp3.rgb *= smp3.a;\n" +
    	    "    sum += smp3 * 2.0;\n" +
    	    "    vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n" +
    	    "    smp4.rgb *= smp4.a;\n" +
    	    "    sum += smp4;\n" +
    	    "    vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n" +
    	    "    smp5.rgb *= smp5.a;\n" +
    	    "    sum += smp5 * 2.0;\n" +
    	    "    vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n" +
    	    "    smp6.rgb *= smp6.a;\n" +
    	    "    sum += smp6;\n" +
    	    "    vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);\n" +
    	    "    smp7.rgb *= smp7.a;\n" +
    	    "    sum += smp7 * 2.0;\n" +
    	    "    vec4 result = sum / 12.0;\n" +
    	    "    gl_FragColor = vec4(result.rgb / result.a, mix(result.a, result.a * (1.0 - texture2D(textureToCheck, gl_TexCoord[0].st).a), check));\n" +
    	    "}\n";

    private final String kawaseDownBloom =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform sampler2D inTexture;\n" +
    	    "uniform vec2 offset, halfpixel, iResolution;\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n" +
    	    "    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st);\n" +
    	    "    sum.rgb *= sum.a;\n" +
    	    "    sum *= 4.0;\n" +
    	    "    vec4 smp1 = texture2D(inTexture, uv - halfpixel.xy * offset);\n" +
    	    "    smp1.rgb *= smp1.a;\n" +
    	    "    sum += smp1;\n" +
    	    "    vec4 smp2 = texture2D(inTexture, uv + halfpixel.xy * offset);\n" +
    	    "    smp2.rgb *= smp2.a;\n" +
    	    "    sum += smp2;\n" +
    	    "    vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n" +
    	    "    smp3.rgb *= smp3.a;\n" +
    	    "    sum += smp3;\n" +
    	    "    vec4 smp4 = texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n" +
    	    "    smp4.rgb *= smp4.a;\n" +
    	    "    sum += smp4;\n" +
    	    "    vec4 result = sum / 8.0;\n" +
    	    "    gl_FragColor = vec4(result.rgb / result.a, result.a);\n" +
    	    "}\n";

    private final String kawaseUp =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform sampler2D inTexture, textureToCheck;\n" +
    	    "uniform vec2 halfpixel, offset, iResolution;\n" +
    	    "uniform int check;\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n" +
    	    "    vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);\n" +
    	    "    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;\n" +
    	    "    sum += texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);\n" +
    	    "    sum += texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;\n" +
    	    "    sum += texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);\n" +
    	    "    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;\n" +
    	    "    sum += texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);\n" +
    	    "    sum += texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;\n" +
    	    "\n" +
    	    "    gl_FragColor = vec4(sum.rgb / 12.0, mix(1.0, texture2D(textureToCheck, gl_TexCoord[0].st).a, check));\n" +
    	    "}\n";

    private final String kawaseDown =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform sampler2D inTexture;\n" +
    	    "uniform vec2 offset, halfpixel, iResolution;\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    vec2 uv = vec2(gl_FragCoord.xy / iResolution);\n" +
    	    "    vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4.0;\n" +
    	    "    sum += texture2D(inTexture, uv - halfpixel.xy * offset);\n" +
    	    "    sum += texture2D(inTexture, uv + halfpixel.xy * offset);\n" +
    	    "    sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);\n" +
    	    "    sum += texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);\n" +
    	    "    gl_FragColor = vec4(sum.rgb * 0.125, 1.0);\n" +
    	    "}\n";

    private final String gradient =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform vec2 location, rectSize;\n" +
    	    "uniform sampler2D tex;\n" +
    	    "uniform vec4 color1, color2, color3, color4;\n" +
    	    "\n" +
    	    "#define NOISE .5/255.0\n" +
    	    "\n" +
    	    "vec3 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){\n" +
    	    "    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);\n" +
    	    "    //Dithering the color from https://shader-tutorial.dev/advanced/color-banding-dithering/\n" +
    	    "    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453));\n" +
    	    "    return color;\n" +
    	    "}\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    vec2 coords = (gl_FragCoord.xy - location) / rectSize;\n" +
    	    "    float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;\n" +
    	    "    gl_FragColor = vec4(createGradient(coords, color1, color2, color3, color4).rgb, texColorAlpha);\n" +
    	    "}\n";

    private final String gaussianBlur =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform sampler2D textureIn;\n" +
    	    "uniform vec2 texelSize, direction;\n" +
    	    "uniform float radius;\n" +
    	    "uniform float weights[256];\n" +
    	    "\n" +
    	    "#define offset texelSize * direction\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    vec3 blr = texture2D(textureIn, gl_TexCoord[0].st).rgb * weights[0];\n" +
    	    "\n" +
    	    "    for (float f = 1.0; f <= radius; f++) {\n" +
    	    "        blr += texture2D(textureIn, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);\n" +
    	    "        blr += texture2D(textureIn, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);\n" +
    	    "    }\n" +
    	    "\n" +
    	    "    gl_FragColor = vec4(blr, 1.0);\n" +
    	    "}\n";

    private final String glow =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform sampler2D textureIn, textureToCheck;\n" +
    	    "uniform vec2 texelSize, direction;\n" +
    	    "uniform vec3 color;\n" +
    	    "uniform bool avoidTexture;\n" +
    	    "uniform float exposure, radius;\n" +
    	    "uniform float weights[256];\n" +
    	    "\n" +
    	    "#define offset direction * texelSize\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    if (direction.y == 1 && avoidTexture) {\n" +
    	    "        if (texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;\n" +
    	    "    }\n" +
    	    "\n" +
    	    "    float innerAlpha = texture2D(textureIn, gl_TexCoord[0].st).a * weights[0];\n" +
    	    "\n" +
    	    "    for (float r = 1.0; r <= radius; r ++) {\n" +
    	    "        innerAlpha += texture2D(textureIn, gl_TexCoord[0].st + offset * r).a * weights[int(r)];\n" +
    	    "        innerAlpha += texture2D(textureIn, gl_TexCoord[0].st - offset * r).a * weights[int(r)];\n" +
    	    "    }\n" +
    	    "\n" +
    	    "    gl_FragColor = vec4(color, mix(innerAlpha, 1.0 - exp(-innerAlpha * exposure), step(0.0, direction.y)));\n" +
    	    "}\n";

    private final String outline =
    	    "#version 120\n" +
    	    "\n" +
    	    "uniform vec2 texelSize, direction;\n" +
    	    "uniform sampler2D texture;\n" +
    	    "uniform float radius;\n" +
    	    "uniform vec3 color;\n" +
    	    "\n" +
    	    "#define offset direction * texelSize\n" +
    	    "\n" +
    	    "void main() {\n" +
    	    "    float centerAlpha = texture2D(texture, gl_TexCoord[0].xy).a;\n" +
    	    "    float innerAlpha = centerAlpha;\n" +
    	    "    for (float r = 1.0; r <= radius; r++) {\n" +
    	    "        float alphaCurrent1 = texture2D(texture, gl_TexCoord[0].xy + offset * r).a;\n" +
    	    "        float alphaCurrent2 = texture2D(texture, gl_TexCoord[0].xy - offset * r).a;\n" +
    	    "\n" +
    	    "        innerAlpha += alphaCurrent1 + alphaCurrent2;\n" +
    	    "    }\n" +
    	    "\n" +
    	    "    gl_FragColor = vec4(color, innerAlpha) * step(0.0, -centerAlpha);\n" +
    	    "}\n";

}