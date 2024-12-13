/* Copyright (c) 2023 BlackBerry Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.good.gd.example.securestore.common_lib.utils;

import com.good.gd.file.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

/**
 * BackupUtils contains a set of useful method for generating sample data in the secure container which can be used
 * by the file backup helper to backup app data.
 */
public class BackupUtils {

	private static final String RootFile = "root.txt";

	private static final int s_aFolderSize = 5;
	private static final int s_bFolderSize = 3;
	private static final int s_cFolderSize = 4;
	
	private static final String s_aFolder = "A";
	private static final String s_bFolder = "B";
	private static final String s_cFolder = "C";

	private static String[] s_content = null;
	private static int s_contentIndex = 0;

    private static final String FILE_CONTENTS = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
            " Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate " +
            " velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." +
            " Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et " +
            " quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores" +
            " eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi" +
            " tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi" +
            "ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum " +
            " fugiat quo voluptas nulla pariatur? ";



	/**
	 * Creates sample data for the secure container. It will create folders and files which will be stored
	 * on the secure container.
	 */
	public static void create() {
		try {
			FileOutputStream out = com.good.gd.file.GDFileSystem.openFileOutput(RootFile, com.good.gd.file.GDFileSystem.MODE_PRIVATE);
			out.write(RootFile.getBytes());
			out.flush();
			out.close();

			createFolder(s_aFolder, s_aFolderSize);
			createFolder(s_bFolder, s_bFolderSize);
			createFolder(s_cFolder, s_cFolderSize);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createFolder(String letter, int quantity) throws IOException {

        // First create the folder
        com.good.gd.file.File file = new com.good.gd.file.File(folderName(letter));
        file.mkdir();

        Random rand = new Random();


        // Then create files in the folder
		for (int i = 0; i < quantity; i++) {
			String filename = letter.toLowerCase() + "_" + i + ".txt";
            FileOutputStream out = com.good.gd.file.GDFileSystem.openFileOutput(folderName(letter) + "/" + filename, com.good.gd.file.GDFileSystem.MODE_PRIVATE);

            //This will write FileHeader to top of filr so we know each file is individual
            String fileHeader = filename + "\n";
            out.write(fileHeader.getBytes());

			/*
			We write a random number of iterations of the Latin placeholder text into each file
			*/

            int numberIterations = rand.nextInt(20);

            for(int j=0; j < numberIterations; j++) {
                out.write(FILE_CONTENTS.getBytes());
            }
            out.flush();
			out.close();
		}
	}
	
	/**
	 * Lists the content which we want to backup/restore. This might be different from the content created and already existing on
	 * the secure container. This is mainly used by the file backup helper to retrieve the files we want to backup/restore.
	 */
	public static String[] list() {
		createList();		
		return s_content;
	}

	private static void createList() {
		s_content = new String[s_aFolderSize + s_cFolderSize + 1];
		s_contentIndex = 0;
		
		createListForFolder(s_aFolder, s_aFolderSize);
		createListForFolder(s_cFolder, s_cFolderSize);
		
		s_content[s_contentIndex] = RootFile;
		s_contentIndex++;
	}
	
	private static void createListForFolder(String letter, int quantity) {
		for (int i = 0; i < quantity; i++) {
			String filename = letter.toLowerCase() + "_" + i + ".txt";			
			s_content[s_contentIndex] = folderName(letter) + "/" + filename;
			s_contentIndex++;
		}
	}
	
	/**
	 * Returns the name of the folder for a given letter.
	 */
	private static String folderName(String letter) {
		return letter + "_Folder";
	}
}
