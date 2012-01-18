/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.eclipse.org/org/documents/epl-v10.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ide.eclipse.adt.internal.resources;



/**
 * Information needed to represent a View or ViewGroup (aka Layout) item
 * in the layout hierarchy, as extracted from the main android.jar and the
 * associated attrs.xml.
 */
public class DeclareStyleableInfo {
    /** The style name, never null. */
    private String mStyleName;
    /** Attributes for this view or view group. Can be empty but never null. */
    private AttributeInfo[] mAttributes;
    /** Short javadoc. Can be null. */
    private String mJavaDoc;
    /** Optional name of the parents stylable. Can be null. */
    private String[] mParents;

    /**
     * Creates a new {@link DeclareStyleableInfo}.
     *
     * @param styleName The name of the style. Should not be empty nor null.
     * @param attributes The initial list of attributes. Can be null.
     */
    public DeclareStyleableInfo(String styleName, AttributeInfo[] attributes) {
        mStyleName = styleName;
        mAttributes = attributes == null ? new AttributeInfo[0] : attributes;
    }

    /** Returns style name */
    public String getStyleName() {
        return mStyleName;
    }

    /** Returns the attributes for this view or view group. Maybe empty but not null. */
    public AttributeInfo[] getAttributes() {
        return mAttributes;
    }

    /** Sets the list of attributes for this View or ViewGroup. */
    public void setAttributes(AttributeInfo[] attributes) {
        mAttributes = attributes;
    }

    /** Returns a short javadoc */
    public String getJavaDoc() {
        return mJavaDoc;
    }

    /** Sets the javadoc. */
    public void setJavaDoc(String javaDoc) {
        mJavaDoc = javaDoc;
    }

    /** Sets the name of the parents styleable. Can be null. */
    public void setParents(String[] parents) {
        mParents = parents;
    }

    /** Returns the name of the parents styleable. Can be null. */
    public String[] getParents() {
        return mParents;
    }
}
