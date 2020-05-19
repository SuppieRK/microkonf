package ru.kugnn.microkonf.config.blocks

class GalleryProperties {
    companion object {
        const val SOURCE = "/config/blocks/gallery.yaml"

        const val STATIC_RESOURCES_FOLDER_NAME = "static"
        const val GALLERY_FOLDER_NAME = "/$STATIC_RESOURCES_FOLDER_NAME/images/gallery/*"
    }

    var enabled: Boolean = false
    lateinit var hashTag: String
    lateinit var description: String
    lateinit var galleryUrl: String

    val galleryImages: Map<String, Int> = {
        val foundImages: Map<String, Int> = emptyMap()/* PathMatchingResourcePatternResolver()
                .getResources(GALLERY_FOLDER_NAME)
                .map { it.file.path.substringAfter(STATIC_RESOURCES_FOLDER_NAME) }
                .mapIndexed { index, path -> path to index }
                .toMap()*/

        if (foundImages.size > 10) {
            throw RuntimeException("Please, use no more than 10 images in your gallery. " +
                    "Having more than 10 images breaks gallery view for iPhone 5 users.")
        } else {
            foundImages
        }
    }.invoke()
}
