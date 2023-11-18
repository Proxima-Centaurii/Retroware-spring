package dev.alphacentaurii.RETROWARE.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class DynamicContentService {
    
    private static final String ROOT_FOLDER = "dynamic";
    private static final String PROFILE_PICTURES = "user_profile_pictures";

    private final ResourceLoader resourceLoader;
    

    @Autowired
    public DynamicContentService(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    public Resource getUserProfilePicture(String username){
        String filename = username.toLowerCase() + ".jpg";
        
        return resourceLoader.getResource("file:" + ROOT_FOLDER + "/" + PROFILE_PICTURES +"/" + filename);
    }

    public void saveUserProfilePicture(MultipartFile file, String username) throws IOException {
        if(file.isEmpty())
            return;

        BufferedImage original = null, scaled = null;
        OutputStream file_out_stream = null;

        try{
            original = ImageIO.read(file.getInputStream());
            scaled = resizeImage(original, 128, 128);

            Path save_path = Paths.get(ROOT_FOLDER, PROFILE_PICTURES, username.toLowerCase()+".jpg");

            file_out_stream = Files.newOutputStream(save_path);
            ImageIO.write(scaled, "jpg", file_out_stream);
        }catch(IOException e){
            throw e;
        } finally{
            //Dispose of buffers
            if(original != null)
                original.flush();

            if(scaled != null)
                scaled.flush();

            if(file_out_stream != null)
                file_out_stream.flush();
        }
        
    }

    public void deleteProfilePicture(String username) throws IOException {
        Files.deleteIfExists(Paths.get(ROOT_FOLDER, PROFILE_PICTURES ,username.toLowerCase() + ".jpg"));
    }

    
    private BufferedImage resizeImage(BufferedImage original, int target_width, int target_height){
        BufferedImage resizedImage = new BufferedImage(target_width, target_height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, target_width, target_height, null);
        g.dispose();

        return resizedImage;
    }


}//End of class
