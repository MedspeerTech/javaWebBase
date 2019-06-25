package com.piotics.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.piotics.model.Conversion;
import com.piotics.model.FileMeta;
import com.piotics.repository.FileMetaMongoRepository;

@Component
public class PresentationProcessor {
	
	@Value("${piotics.storage.location}")
	String storageLocationPath;
	
	@Autowired
	FileMetaMongoRepository fileMetaMongoRepository;

	public void convetPptToImages(Conversion conversion) throws IOException {
				
		File inputFile = null;
		File dir = new File(storageLocationPath+
				conversion.getSourceLocation().substring(0, conversion.getSourceLocation().lastIndexOf("/")));
		File[] directoryListing = dir.listFiles();
		for (File child : directoryListing) {

			String dirName = dir.getPath().substring(dir.getPath().lastIndexOf("/") + 1);
			if (child.getName().contains("original"))
				inputFile = child;
		}

		FileInputStream file = new FileInputStream(inputFile);
		String fileExt = FilenameUtils.getExtension(inputFile.getAbsolutePath());
		if (fileExt.equals("ppt")) {
			convertppt(file, conversion);

		} else if (fileExt.equals("pptx")) {
			convertpptx(file, conversion);
		}
		
	}

	private void convertpptx(FileInputStream file, Conversion conversion) throws IOException {

		XMLSlideShow ppt = new XMLSlideShow(file);

		File dir = new File(storageLocationPath+
				conversion.getSourceLocation().substring(0, conversion.getSourceLocation().lastIndexOf("/") + 1));
		// getting the dimensions and size of the slide
		Dimension pgsize = ppt.getPageSize();
		java.util.List<XSLFSlide> slides = ppt.getSlides();

		Iterator<XSLFSlide> itr = slides.iterator();
		int slideNo = 1;

		while (itr.hasNext()) {
			XSLFSlide slide = itr.next();

			BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = img.createGraphics();

			// clear the drawing area
			graphics.setPaint(Color.white);
			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

			// render
			slide.draw(graphics);

			FileOutputStream out = new FileOutputStream(dir.getPath() + "/" + slideNo + ".jpg");
			slideNo = slideNo + 1;
			javax.imageio.ImageIO.write(img, "jpg", out);
			ppt.write(out);

			out.close();
		}
	}

	private void convertppt(FileInputStream file, Conversion conversion) throws IOException {

		HSLFSlideShow ppt = new HSLFSlideShow(file);

		File dir = new File(storageLocationPath+
				conversion.getSourceLocation().substring(0, conversion.getSourceLocation().lastIndexOf("/") + 1));
		// getting the dimensions and size of the slide
		Dimension pgsize = ppt.getPageSize();
		List<HSLFSlide> slides = ppt.getSlides();

		Iterator<HSLFSlide> itr = slides.iterator();
		int slideNo = 1;

		while (itr.hasNext()) {
			HSLFSlide slide = itr.next();

			BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = img.createGraphics();

			// clear the drawing area
			graphics.setPaint(Color.white);
			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

			// render
			slide.draw(graphics);

			FileOutputStream out = new FileOutputStream(dir.getPath() + "/" + slideNo + ".jpg");
			slideNo = slideNo + 1;
			javax.imageio.ImageIO.write(img, "jpg", out);
			ppt.write(out);

			out.close();

		}
	}

	public void generatePreviewAndSetTotalSlides(FileMeta fileMeta) {

		String path = fileMeta.getPath().substring(0, fileMeta.getPath().lastIndexOf("/"));
		String filePath = storageLocationPath+fileMeta.getPath();
		String fileExt = fileMeta.getName().substring(fileMeta.getName().indexOf(46) + 1, fileMeta.getName().length());

		File inputFile = new File(filePath+"original."+fileExt);
		FileInputStream file = null;
		int totalSlides = 0;
		try {
			file = new FileInputStream(inputFile);

			if (fileExt.equals("ppt")) {
				totalSlides = getPreviewForPPT(file, fileMeta.getPath());
			} else if (fileExt.equals("pptx")) {
				totalSlides = getPreviewForPPTX(file, path);
			}
			fileMeta.setLength(totalSlides);
			fileMetaMongoRepository.save(fileMeta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private int getPreviewForPPTX(FileInputStream file, String path) {

		XMLSlideShow ppt = null;
		int totalSlides = 0;

		try {
			ppt = new XMLSlideShow(file);
			Dimension pgsize = ppt.getPageSize();
			java.util.List<XSLFSlide> slides = ppt.getSlides();
			totalSlides = slides.size();

			XSLFSlide slide = slides.get(0);

			BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = img.createGraphics();

			// clear the drawing area
			graphics.setPaint(Color.white);
			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

			// render
			slide.draw(graphics);

			path = path+"/";
			FileOutputStream out = new FileOutputStream(storageLocationPath+path + "file_preview.jpg");

			javax.imageio.ImageIO.write(img, "jpg", out);
			ppt.write(out);

			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return totalSlides;
	}

	private int getPreviewForPPT(FileInputStream file, String path) {
		HSLFSlideShow ppt = null;
		int totalSlides = 0;

		try {
			ppt = new HSLFSlideShow(file);
			Dimension pgsize = ppt.getPageSize();
			java.util.List<HSLFSlide> slides = ppt.getSlides();

			HSLFSlide slide = slides.get(0);
			totalSlides = slides.size();

			BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = img.createGraphics();

			// clear the drawing area
			graphics.setPaint(Color.white);
			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

			// render
			slide.draw(graphics);

			path = path.substring(0, path.lastIndexOf("/")+1);
			FileOutputStream out = new FileOutputStream(storageLocationPath+path + "file_preview.jpg");

			javax.imageio.ImageIO.write(img, "jpg", out);
			ppt.write(out);
			
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return totalSlides;
	}

}
