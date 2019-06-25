package com.piotics.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.piotics.model.FileMeta;
import com.piotics.repository.ConversionMongoRepository;
import com.piotics.model.Conversion;
import com.piotics.model.EMail;
import com.piotics.common.MailManager;
import com.piotics.constants.FileType;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Service
public class ConversionsService {

	@Value("${piotics.storage.location}")
	String storageLocationPath;
	
	@Value("${video.path.ffmpeg}")
	String ffmpegPath;

	@Value("${video.path.ffprobe}")
	String ffprobePath;
	
	@Autowired
	PresentationService presentationService;
	
	@Autowired
	ConversionMongoRepository conversionMongoRepository;
	
	@Autowired
	MailManager mailManager;
	
	@Autowired
	PdfService pdfService;
	
	@Autowired
	WordService wordService;
	
	@Autowired
	VideoProcessor videoProcessor;
	
	private static boolean isConversionInProgress = false;
	
	public void genratePreview(FileMeta fileMeta) {

		String folderRelativePath = fileMeta.getPath();

		try {
			if (fileMeta.getType() == FileType.Video) {
				String folderFullpath = storageLocationPath + folderRelativePath;
				File inputFile = null;

				File dir = new File(folderFullpath);
				File[] directoryListing = dir.listFiles();
				for (File child : directoryListing) {
					if (child.getName().contains("original"))
						inputFile = child;
				}

				FFprobe ffprobe = new FFprobe(ffprobePath);
				FFmpeg ffmpeg = new FFmpeg(ffmpegPath);

				FFmpegBuilder builder = new FFmpegBuilder().addInput(inputFile.getPath())
						.addOutput(folderFullpath + "file_preview.jpg").setFrames(1)
						.setVideoFilter("select='gte(n\\,10)',scale=200:-1").done();
				FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
				executor.createJob(builder).run();
			} else if (fileMeta.getType() == FileType.Presentation) {
				presentationService.initiatePresentationConversion();
				presentationService.generatePreview(fileMeta);
			} else if (fileMeta.getType() == FileType.PDF) {
				pdfService.initiatePdfConversion();
				pdfService.generatePreview(fileMeta);
			} else if (fileMeta.getType() == FileType.Document) {
				wordService.generatePreview(fileMeta);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void convertVideos() {

		boolean isConverted = false;

		Conversion con = null;
		FileMeta fileMeta = new FileMeta();
		try {
			while (!isConversionInProgress) {
				List<Conversion> filesToConvert = conversionMongoRepository.findByFileTypeAndLogNull(FileType.Video);

				if (!filesToConvert.isEmpty()) {

					for (Conversion fileInProcess : filesToConvert) {

						con = fileInProcess;

						isConversionInProgress = true;
						String srcLocation = fileInProcess.getSourceLocation();
						String fileName = srcLocation.substring(srcLocation.indexOf('/') + 1, srcLocation.length() - 1);

						File inputFile = null;
						File dir = new File(storageLocationPath + srcLocation);
						File[] directoryListing = dir.listFiles();
						for (File child : directoryListing) {
							if (child.getName().contains("original"))
								inputFile = child;
						}

						try {

							videoProcessor.saveDuration(inputFile);
							isConverted = convertToWebm(inputFile, fileInProcess);
							isConverted = convertToMp4(inputFile, fileInProcess);

							conversionMongoRepository.delete(fileInProcess);
						} catch (Exception e) {

							if (e.getMessage() == null) {

								fileInProcess.setLog("something went wrong");
							} else {
								fileInProcess.setLog(e.getMessage());
							}
							conversionMongoRepository.save(fileInProcess);
						}

					}
					isConversionInProgress = false;
					convertVideos();
				} else {
					break;
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			if (e.getMessage() == null) {

				con.setLog("something went wrong");
			} else {
				con.setLog(e.getMessage());
			}
			conversionMongoRepository.save(con);
			
			EMail eMail = mailManager.composeConversionFailureNotifcationMail("video, conversionId :", con.getId(),
					con.getSourceLocation());
			mailManager.sendEmail(eMail);
		}
	}

	private boolean convertToMp4(File inputFile, Conversion fileInProcess) {

		try {

			FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
			FFprobe ffprobe = new FFprobe(ffprobePath);

//			String fileName = fileInProcess.getSourceLocation()
//					.substring(fileInProcess.getSourceLocation().lastIndexOf("/") + 1);

			FFmpegBuilder builder = new FFmpegBuilder().setInput(inputFile.getAbsolutePath()) // Filename, or a
					// FFmpegProbeResult
					.setInput(inputFile.getAbsolutePath()) // Filename, or a FFmpegProbeResult
					.overrideOutputFiles(true) // Override the output if it exists

					.addOutput(storageLocationPath + fileInProcess.getSourceLocation() + "file" + ".mp4") // Filename
					// for
					// the
					// destination
					.setFormat("mp4") // Format is inferred from filename, or can be set
//                .setTargetSize(250_000)  // Aim for a 250KB file

					.disableSubtitle() // No subtiles

					.setAudioChannels(1) // Mono audio
					.setAudioCodec("aac") // using the aac codec
//                .setAudioSampleRate(48_000)  // at 48KHz
					.setAudioBitRate(32768) // at 32 kbit/s

//                .setVideoCodec("libx264")     // Video using x264
					.setVideoFrameRate(24, 1) // at 24 frames per second
					.setVideoResolution(640, 480) // at 640x480 resolution

					.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
					.done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			executor.createJob(builder).run();

			conversionMongoRepository.delete(fileInProcess);

			return true;

		} catch (IOException e) {

			e.printStackTrace();

			if (e.getMessage() == null) {

				fileInProcess.setLog("something went wrong");
			} else {
				fileInProcess.setLog(e.getLocalizedMessage());
			}
			conversionMongoRepository.save(fileInProcess);

			EMail eMail = mailManager.composeConversionFailureNotifcationMail("video to mp4, conversionId :",
					fileInProcess.getId(), fileInProcess.getSourceLocation());
			mailManager.sendEmail(eMail);

			return false;

		} catch (Exception e) {

			e.printStackTrace();
			if (e.getMessage() == null) {

				fileInProcess.setLog("something went wrong");
			} else {
				fileInProcess.setLog(e.getLocalizedMessage());
			}
			conversionMongoRepository.save(fileInProcess);

			EMail eMail = mailManager.composeConversionFailureNotifcationMail("video to mp4, conversionId :",
					fileInProcess.getId(), fileInProcess.getSourceLocation());
			mailManager.sendEmail(eMail);

			return false;
		}

	}

	private boolean convertToWebm(File inputFile, Conversion fileInProcess) {

		try {
			FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
			FFprobe ffprobe = new FFprobe(ffprobePath);

			FFmpegBuilder builder = new FFmpegBuilder().setInput(inputFile.getAbsolutePath()) // Filename, or a
					// FFmpegProbeResult
					.overrideOutputFiles(true) // Override the output if it exists

					.addOutput(storageLocationPath + fileInProcess.getSourceLocation() + "file" + ".webm") // Filename
																											// for the
					// destination
					.setFormat("webM") // Format is inferred from filename, or can be set
					.setVideoCodec("vp8")
//            .setTargetSize(250_000)  // Aim for a 250KB file

					.disableSubtitle() // No subtitles

					.setAudioChannels(1) // Mono audio
					.setAudioCodec("libvorbis") // using the aac codec
//            .setAudioSampleRate(48_000)  // at 48KHz
					.setAudioBitRate(32768) // at 32 kbit/s

//            .setVideoCodec("libx264")     // Video using x264
					.setVideoFrameRate(24, 1) // at 24 frames per second
					.setVideoResolution(640, 480) // at 640x480 resolution

					.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
					.done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			executor.createJob(builder).run();

			conversionMongoRepository.delete(fileInProcess);

			return true;

		} catch (IOException e) {

			e.printStackTrace();

			if (e.getMessage() == null) {

				fileInProcess.setLog("something went wrong");

			} else {

				fileInProcess.setLog(e.getLocalizedMessage());
			}
			conversionMongoRepository.save(fileInProcess);

			EMail eMail = mailManager.composeConversionFailureNotifcationMail("video to webM, conversionId :",
					fileInProcess.getId(), fileInProcess.getSourceLocation());
			mailManager.sendEmail(eMail);

			return false;

		} catch (Exception e) {

			e.printStackTrace();
			if (e.getMessage() == null) {

				fileInProcess.setLog("something went wrong");
			} else {

				fileInProcess.setLog(e.getLocalizedMessage());
			}
			conversionMongoRepository.save(fileInProcess);

			EMail eMail = mailManager.composeConversionFailureNotifcationMail("video to webM, conversionId :",
					fileInProcess.getId(), fileInProcess.getSourceLocation());
			mailManager.sendEmail(eMail);

			return false;
		}

	}

}
