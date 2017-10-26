package org.jaudiotagger.audio.aiff;

import java.util.List;

import org.jaudiotagger.audio.generic.AbstractTag;
import org.jaudiotagger.audio.generic.GenericTag;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.images.Artwork;

public class AiffTag extends GenericTag {

    private class AiffTagTextField implements TagTextField
    {

        /**
         * Stores the string.
         */
        private String content;

        /**
         * Stores the identifier.
         */
        private final String id;

        /**
         * Creates an instance.
         *
         * @param fieldId        The identifier.
         * @param initialContent The string.
         */
        public AiffTagTextField(String fieldId, String initialContent)
        {
            this.id = fieldId;
            this.content = initialContent;
        }

        /**
         * (overridden)
         *
         * @see TagField#copyContent(TagField)
         */
        public void copyContent(TagField field)
        {
            if (field instanceof TagTextField)
            {
                this.content = ((TagTextField) field).getContent();
            }
        }

        /**
         * (overridden)
         *
         * @see TagTextField#getContent()
         */
        public String getContent()
        {
            return this.content;
        }

        /**
         * (overridden)
         *
         * @see TagTextField#getEncoding()
         */
        public String getEncoding()
        {
            return "ISO-8859-1";
        }

        /**
         * (overridden)
         *
         * @see TagField#getId()
         */
        public String getId()
        {
            return id;
        }

        /**
         * (overridden)
         *
         * @see TagField#getRawContent()
         */
        public byte[] getRawContent()
        {
            return this.content == null ? new byte[]{} : Utils.getDefaultBytes(this.content, getEncoding());
        }

        /**
         * (overridden)
         *
         * @see TagField#isBinary()
         */
        public boolean isBinary()
        {
            return false;
        }

        /**
         * (overridden)
         *
         * @see TagField#isBinary(boolean)
         */
        public void isBinary(boolean b)
        {
            /* not supported */
        }

        /**
         * (overridden)
         *
         * @see TagField#isCommon()
         */
        public boolean isCommon()
        {
            return true;
        }

        /**
         * (overridden)
         *
         * @see TagField#isEmpty()
         */
        public boolean isEmpty()
        {
            return this.content.equals("");
        }

        /**
         * (overridden)
         *
         * @see TagTextField#setContent(String)
         */
        public void setContent(String s)
        {
            this.content = s;
        }

        /**
         * (overridden)
         *
         * @see TagTextField#setEncoding(String)
         */
        public void setEncoding(String s)
        {
            /* Not allowed */
        }

        /**
         * (overridden)
         *
         * @see Object#toString()
         */
        public String toString()
        {
            return getContent();
        }
    } // End of AiffTagTextField
    
    
    public boolean hasField(AiffTagFieldKey fieldKey)
    {
        return hasField(fieldKey.name());
    }
    
    /**
     * Create new AIFF-specific field and set it in the tag
     *
     * @param genericKey
     * @param value
     * @throws KeyNotFoundException
     * @throws FieldDataInvalidException
     */
    public void setField(AiffTagFieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
        TagField tagfield = createField(genericKey,value);
        setField(tagfield);
    }
    
    public TagField createField(AiffTagFieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
            return new AiffTagTextField(genericKey.name(),value);
    }

    public TagField createCompilationField(boolean value) throws KeyNotFoundException, FieldDataInvalidException
    {
        return createField(FieldKey.IS_COMPILATION,String.valueOf(value));
    }

}
