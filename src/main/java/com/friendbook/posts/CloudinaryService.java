package com.friendbook.posts;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

	private final Cloudinary cloudinary;

	public CloudinaryUploadResult uploadImage(MultipartFile file) {

		try {

			Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.asMap("resource_type", "image"));
			return new CloudinaryUploadResult(result.get("secure_url").toString(), result.get("public_id").toString());
		} catch (IOException e) {
			throw new RuntimeException("Image upload failed", e);
		}
	}

	public void deletePost(Post post) throws IOException {
		cloudinary.uploader().destroy(post.getImagePublicId(), ObjectUtils.emptyMap());
	}
}