package com.piotics.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.piotics.model.FileMeta;
import com.piotics.repository.FileMetaMongoRepository;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

@Component
public class VideoProcessor {
	
	@Value("${piotics.storage.location}")
	String storageLocationPath;

	@Value("${video.path.ffprobe}")
	String ffProbepath;
	
	@Autowired
	FileMetaMongoRepository fileMetaMongoRepository;

	public void saveDuration(File inputFile) {

		String FMstoragePath = inputFile.getPath().replace(storageLocationPath, "") ;
		FMstoragePath = FMstoragePath.replace(inputFile.getName(), "");
		
		FileMeta fileMeta = fileMetaMongoRepository.findByPath(FMstoragePath);
		
		String path = storageLocationPath + fileMeta.getPath()+fileMeta.getName();

		try {

			File file = new File(path);

			FFprobe ffprobe = new FFprobe(ffProbepath);

			FFmpegProbeResult probeResult = ffprobe.probe(file.getPath());

			FFmpegFormat format = probeResult.getFormat();

			long duration = Math.round(format.duration);
		
			fileMeta.setLength(duration);
			
			fileMetaMongoRepository.save(fileMeta);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
