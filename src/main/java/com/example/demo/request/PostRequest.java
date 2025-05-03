package com.example.demo.request;

import java.util.List;



public class PostRequest {
	  private String title;
	    private String content;
	    private String mainImage; 
	    private String imageTitle;  // Tiêu đề ảnh
	    private List<SubtitleRequest> subtitles;

	    // Getters and Setters
	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content;
	    }

	    public String getMainImage() {
	        return mainImage;
	    }

	    public String getImageTitle() {
			return imageTitle;
		}

		public void setImageTitle(String imageTitle) {
			this.imageTitle = imageTitle;
		}

		public void setMainImage(String mainImage) {
	        this.mainImage = mainImage;
	    }

	    public List<SubtitleRequest> getSubtitles() {
	        return subtitles;
	    }

	    public void setSubtitles(List<SubtitleRequest> subtitles) {
	        this.subtitles = subtitles;
	    }

		@Override
		public String toString() {
			return "PostRequest [title=" + title + ", content=" + content + ", mainImage=" + mainImage + ", subtitles="
					+ subtitles + "]";
		}
}
