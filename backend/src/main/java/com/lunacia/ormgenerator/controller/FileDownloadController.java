package com.lunacia.ormgenerator.controller;


import com.lunacia.ormgenerator.utils.ZipUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Map;

@RestController
@CrossOrigin
public class FileDownloadController {

	//下载
	@PostMapping("/download")
	public ResponseEntity<FileSystemResource> download(@RequestBody Map<String, String> body,
	                                                   HttpServletResponse response) {


		try {
			String packageName = URLDecoder.decode(body.get("package"), "utf-8");
			String path = ResourceUtils.getURL("classpath:").getPath();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String filePath = ZipUtil.generateFile(path + sdf.format(System.currentTimeMillis())+"/"+packageName, "zip");
			File file = new File(filePath);

			// 获取本地文件系统中的文件资源
			FileSystemResource resource = new FileSystemResource(file.getAbsolutePath());

			// 解析文件的 mime 类型
			String mediaTypeStr = URLConnection.getFileNameMap().getContentTypeFor(file.getName());
			// 无法判断MIME类型时，作为流类型
			mediaTypeStr = (mediaTypeStr == null) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mediaTypeStr;
			// 实例化MIME
			MediaType mediaType = MediaType.parseMediaType(mediaTypeStr);


			/*
			 * 构造响应的头
			 */
			HttpHeaders headers = new HttpHeaders();
			// 下载之后需要在请求头中放置文件名，该文件名按照ISO_8859_1编码。
			String filenames = new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
			headers.setContentDispositionFormData("attachment", filenames);
			headers.setContentType(mediaType);


			/*
			 * 返还资源
			 */
			return ResponseEntity.ok()
					.headers(headers)
					.contentLength(resource.getInputStream().available())
					.body(resource);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.badRequest().build();
	}
}
