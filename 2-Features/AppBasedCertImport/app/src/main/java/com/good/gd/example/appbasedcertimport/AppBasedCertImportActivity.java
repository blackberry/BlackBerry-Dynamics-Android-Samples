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

package com.good.gd.example.appbasedcertimport;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.pki.Certificate;
import com.good.gd.pki.CertificateHandler;
import com.good.gd.pki.CertificateListener;
import com.good.gd.pki.Credential;
import com.good.gd.pki.CredentialException;
import com.good.gd.pki.CredentialsProfile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/* AppBasedCertImportActivity - the entry point activity which will start authorization with Blackberry Dynamics
 * and once done launch the application UI.
 */
public class AppBasedCertImportActivity extends SampleAppActivity implements GDStateListener, CertificateListener,
		ProfilesFragment.ProfilesListener, CertificatesListFragment.UCPListener, AppBasedCertImportApplication.UcpUpdateListener {

	public final static int CERT_IMPORT_READ_REQUEST_CODE = 100;
	public final static int CERT_IMPORT_TO_UCP_READ_REQUEST_CODE = 101;
	public final static String STARTED_WITH_UCP = "STARTED_WITH_UCP";
	private static final String TAG = AppBasedCertImportActivity.class.getSimpleName();
	private ProfilesFragment profilesFragment;
	private CertificatesListFragment mCertificatesListFragment;
	private FragmentManager mFragmentManager;
	private boolean startedWithUCP = false;


	public AppBasedCertImportActivity() {
		profilesFragment = new ProfilesFragment();
		profilesFragment.setProfilesListener(this);
		mCertificatesListFragment = new CertificatesListFragment();
		mCertificatesListFragment.setListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GDAndroid.getInstance().activityInit(this);
		setContentView(R.layout.activity_main);
		mFragmentManager = getSupportFragmentManager();
		if (mFragmentManager.getBackStackEntryCount() == 0) {
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.main_activity, profilesFragment);
			fragmentTransaction.commit();
		}
		AppBasedCertImportApplication.getInstance().setUcpUpdateListener(this);
		startedWithUCP = getIntent().getBooleanExtra(STARTED_WITH_UCP, false);

		setupAppBar(getString(R.string.app_name));

		ViewGroup mainView = findViewById(R.id.main_activity);
		ViewGroup contentView = findViewById(R.id.profiles_list_layout);

		adjustViewsIfEdgeToEdgeMode(mainView, null, contentView);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		startedWithUCP = intent.getBooleanExtra(STARTED_WITH_UCP, false);
		setIntent(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppBasedCertImportApplication.getInstance().setUcpUpdateListener(null);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Get available the user credential profiles as a map {key:UCP_ID, value:UCP}
		if(AppBasedCertImportApplication.getInstance().isAppAuthorized()) {
			Collection<CredentialsProfile> credentialProfiles = CredentialsProfile.getMap().values();
			profilesFragment.refreshData(new ArrayList<>(credentialProfiles));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
    protected void onPostResume() {
        super.onPostResume();
		if ((mFragmentManager.getBackStackEntryCount() != 0) && startedWithUCP) {
			mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.main_activity, profilesFragment);
			fragmentTransaction.commit();
			getIntent().removeExtra(STARTED_WITH_UCP);
			startedWithUCP = false;
		}
		if (startedWithUCP) {
			showMessage("Other application requested the certificate(s)", false);
		}
    }

	@Override
	protected void onStop() {
		super.onStop();
		// Remove the subscription for the events about adding or removing the certificate.
		CertificateHandler.getInstance().removeCertificateListener(this);
		profilesFragment.resetImportRequiredMarkers();
	}

	/*
     * Activity specific implementation of GDStateListener.
     *
     * If a singleton event Listener is set by the application (as it is in this case) then setting
     * Activity specific implementations of GDStateListener is optional
     */
	@Override
	public void onAuthorized() {
		//If Activity specific GDStateListener is set then its onAuthorized( ) method is called when
		//the activity is started if the App is already authorized
		Log.i(TAG, "onAuthorized()");

		// Subscribe to the events about adding or removing the certificate once the app is authorized
		CertificateHandler.getInstance().addCertificateListener(this);

		Collection<CredentialsProfile> credentialProfiles = CredentialsProfile.getMap().values();
		profilesFragment.refreshData(new ArrayList<>(credentialProfiles));
	}

	@Override
	public void onLocked() {
		Log.i(TAG, "onLocked()");
	}

	@Override
	public void onWiped() {
		Log.i(TAG, "onWiped()");
	}

	@Override
	public void onUpdateConfig(Map<String, Object> settings) {
		Log.i(TAG, "onUpdateConfig()");
	}

	@Override
	public void onUpdatePolicy(Map<String, Object> policyValues) {
		Log.i(TAG, "onUpdatePolicy()");
	}

	@Override
	public void onUpdateServices() {
		Log.i(TAG, "onUpdateServices()");
	}

    @Override
    public void onUpdateEntitlements() {
        Log.i(TAG, "onUpdateEntitlements()");
    }

    @Override
    public void onCertificatedAdded(final Certificate certificate) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showMessage(certificate.getSubjectName() + " added", false);
			}
		});
	}

    @Override
    public void onCertificateRemoved(final Certificate certificate) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showMessage(certificate.getSubjectName() + " removed", false);
			}
		});
    }

	@Override
	public void onProfileSelected(final CredentialsProfile credentialsProfile) {
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.main_activity, mCertificatesListFragment);
		fragmentTransaction.addToBackStack(CertificatesListFragment.TAG);
		fragmentTransaction.commit();
		mCertificatesListFragment.refreshData(credentialsProfile);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CERT_IMPORT_READ_REQUEST_CODE && resultCode == FragmentActivity.RESULT_OK) {
			final List<Uri> uriList = extractUris(data);
			PasswordDialog passwordDialog = new PasswordDialog(this, new PasswordDialog.PasswordDialogListener() {
				@Override
				public void onResult(String result) {
					importCertFromFile(uriList, result, null);
				}
			});
			passwordDialog.show();
		} else if (requestCode == CERT_IMPORT_TO_UCP_READ_REQUEST_CODE && resultCode == FragmentActivity.RESULT_OK) {
			final List<Uri> uriList = extractUris(data);
			PasswordDialog passwordDialog = new PasswordDialog(this, new PasswordDialog.PasswordDialogListener() {
				@Override
				public void onResult(String result) {
					importCertFromFile(uriList, result, mCertificatesListFragment.getUserCredentialsProfileId());
				}
			});
			passwordDialog.show();
		}
	}

	private List<Uri> extractUris(Intent data) {
		ArrayList<Uri> result = new ArrayList<>();
		if (data.getData() != null) {
			result.add(data.getData());
			return result;
		}
		ClipData clipData = data.getClipData();
		if (clipData != null) {
			for (int i = 0; i < clipData.getItemCount(); i++) {
				ClipData.Item item = clipData.getItemAt(i);
				Uri uri = item.getUri();
				result.add(uri);
			}
		}
		return result;
	}

	private void importCertFromFile(List<Uri> uriList, String password, String ucp_id) {
		Log.i(TAG, "Uri: " + uriList.toString());
		try {
			for (Uri uri:uriList) {
				String filePath = uri.toString();
				String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
				InputStream is = getContentResolver().openInputStream(uri);
				int certBufferLength = is.available();
				byte[] buffer = new byte[certBufferLength];
				is.read(buffer);
				String importedUCP_ID;
				if (ucp_id == null || ucp_id.isEmpty()) {
					if (extension.compareTo("pem") == 0) {
						// Import PEM data to best matching UCP
						importedUCP_ID = Credential.importPEM(buffer, password, null);
					} else {
						// Otherwise assume PKCS12
						importedUCP_ID = Credential.importPKCS12(buffer, password);
					}
				} else {
					if (extension.compareTo("pem") == 0) {
						// Import PEM data to specified UCP
						importedUCP_ID = Credential.importPEM(buffer, password, ucp_id);
					}  else {
						// Otherwise assume PKCS12
						Credential.importPKCS12(buffer, password, ucp_id);
						importedUCP_ID = ucp_id;
					}
				}
				CredentialsProfile importedUCP = CredentialsProfile.getMap().get(importedUCP_ID);
				if (importedUCP != null) {
					showMessage("Imported certificate \"" + uri.getLastPathSegment() + "\" " +
							"to UCP: \"" + importedUCP.getName() + "\"", false);
				} else {
					showMessage("Cannot map the certificate \"" + uri.getLastPathSegment() + "\" to any UCP", true);
				}

			}
			if (profilesFragment.isImportNow()) {
				Credential.finalizeImport();
			}
		} catch (CredentialException | FileNotFoundException e) {
			e.printStackTrace();
            showMessage("Error: " + e.toString(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onCertImportClicked(View view) {
		switch (view.getId()) {
			case R.id.btn_cert_import:
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				intent.setType("application/x-pkcs12/*");
				startActivityForResult(intent, CERT_IMPORT_READ_REQUEST_CODE);
				break;
			case R.id.btn_finish_import:
				Credential.finalizeImport();
				break;
		}
	}

	@Override
	public void onCertificateSelected(Certificate certificate) {
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		CertificateItemFragment certificateItemFragment = new CertificateItemFragment();
		certificateItemFragment.setData(certificate);
		fragmentTransaction.replace(R.id.main_activity, certificateItemFragment);
		fragmentTransaction.addToBackStack(CertificateItemFragment.TAG);
		fragmentTransaction.commit();
	}

	@Override
	public void onImportToUCP() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		intent.setType("application/x-pkcs12/*");
		startActivityForResult(intent, CERT_IMPORT_TO_UCP_READ_REQUEST_CODE);
	}

	@Override
	public void onUndoImport() {
		try {
			String profileId = mCertificatesListFragment.getUserCredentialsProfileId();
			Credential.undoImport(profileId);
		}
		catch (CredentialException e) {
			e.printStackTrace();
			showMessage("Error: " + e.toString(), true);
		}
	}

	public void showMessage(String msg, boolean isError) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onUCPUpdated(Intent intent) {
		if (CredentialsProfile.getState(intent) != CredentialsProfile.State.GDCredentialsProfileStateNone) {
			Collection<CredentialsProfile> credentialProfiles = CredentialsProfile.getMap().values();
			profilesFragment.refreshData(new ArrayList<>(credentialProfiles));
		}
		switch (CredentialsProfile.getState(intent)) {
			case GDCredentialsProfileStateImportNow:
				profilesFragment.markImportRequired(CredentialsProfile.getId(intent));
				break;
			case GDCredentialsProfileStateImported:
				profilesFragment.unmarkImportRequired(CredentialsProfile.getId(intent));
			case GDCredentialsProfileStateDeleted:
				profilesFragment.unmarkImportRequired(CredentialsProfile.getId(intent));
				break;
		}
	}
}
