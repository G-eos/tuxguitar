/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

import com.sun.media.sound.ModelInstrumentComparator;
import com.sun.media.sound.ModelPatch;



/**
 * A simple soundbank that contains instruments and soundbankresources.
 *
 * @author Karl Helgason
 */
public class SimpleSoundbank implements Soundbank {

    String name = "";
    String version = "";
    String vendor = "";
    String description = "";
    List<SoundbankResource> resources = new ArrayList<SoundbankResource>();
    List<Instrument> instruments = new ArrayList<Instrument>();

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getVendor() {
        return vendor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SoundbankResource[] getResources() {
        return resources.toArray(new SoundbankResource[resources.size()]);
    }

    public Instrument[] getInstruments() {
        Instrument[] inslist_array
                = instruments.toArray(new Instrument[resources.size()]);
        Arrays.sort(inslist_array, new ModelInstrumentComparator());
        return inslist_array;
    }

    public Instrument getInstrument(Patch patch) {
        int program = patch.getProgram();
        int bank = patch.getBank();
        boolean percussion = false;
        if (patch instanceof ModelPatch)
            percussion = ((ModelPatch)patch).isPercussion();
        for (Instrument instrument : instruments) {
            Patch patch2 = instrument.getPatch();
            int program2 = patch2.getProgram();
            int bank2 = patch2.getBank();
            if (program == program2 && bank == bank2) {
                boolean percussion2 = false;
                if (patch2 instanceof ModelPatch)
                    percussion2 = ((ModelPatch)patch2).isPercussion();
                if (percussion == percussion2)
                    return instrument;
            }
        }
        return null;
    }

    public void addResource(SoundbankResource resource) {
        if (resource instanceof Instrument)
            instruments.add((Instrument) resource);
        else
            resources.add(resource);
    }

    public void removeResource(SoundbankResource resource) {
        if (resource instanceof Instrument)
            instruments.remove((Instrument) resource);
        else
            resources.remove(resource);
    }

    public void addInstrument(Instrument resource) {
        instruments.add(resource);
    }

    public void removeInstrument(Instrument resource) {
        instruments.remove(resource);
    }

    public void addAllInstruments(Soundbank soundbank) {
        for (Instrument ins : soundbank.getInstruments())
            addInstrument(ins);
    }

    public void removeAllInstruments(Soundbank soundbank) {
        for (Instrument ins : soundbank.getInstruments())
            removeInstrument(ins);
    }
}
