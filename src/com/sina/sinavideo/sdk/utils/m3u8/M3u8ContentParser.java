package com.sina.sinavideo.sdk.utils.m3u8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.sina.sinavideo.coreplayer.util.LogS;
import com.sina.sinavideo.sdk.data.VDResolutionData;
import com.sina.sinavideo.sdk.utils.VDLog;

import android.content.Context;
import android.os.AsyncTask;

/**
 * 解析逻辑
 * 
 * @author GengHongchao
 * 
 */
public class M3u8ContentParser {

	private static final String TAG = "M3u8ContentParser";
	private static HttpClient sHttpClient;
	private M3u8AsyncTask mTask = null;

	private static final int CONNETED_TIMEOUT = 5;
	private static final int RETRY_TIMES = 5;

	public M3u8ContentParser(M3u8ParserListener listener, String _vid,
			Context ctt) {
		mListener = listener;
		mListener.updateVideoID(_vid);
		if (sHttpClient == null) {
			sHttpClient = createHttpClient();
		}
	}

	private M3u8ParserListener mListener;

	public interface M3u8ParserListener {

		public void onParcelResult(String url, VDResolutionData content);

		public void onError(int error_msg);

		public void updateVideoPlayUrl(String playUrl);

		public void updateVideoID(String videoId);
	}

	/**
	 * 无数据错误
	 */
	public static final int ERROR_NO_CONTENT = 1;
	/**
	 * 解析错误
	 */
	public static final int ERROR_PARSE = 2;

	public void startParserM3u8(String url) {
		mListener.updateVideoPlayUrl(url);
		mTask = new M3u8AsyncTask(url);
		mTask.execute();
	}

	public void cancelParserM3U8() {
		if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
			mTask.cancel(true);
		}
	}

	private VDResolutionData parse(InputStream is) {
		if (is == null) {
			mListener.onError(ERROR_NO_CONTENT);
			// return new M3u8Content();
			return new VDResolutionData();
		}

		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(is));

			int m3u8Type = M3u8Content.M3U8_TYPE_UNKNOWN;
			VDResolutionData resolutionData = null;
			VDResolutionData.VDResolution resolution = null;
			String line = null;
			// int idx = 0;
			int resolutionCount = 0;
			String resolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
			while ((line = br.readLine()) != null) {
				VDLog.d(TAG, line);
				if (line.startsWith("#EXT-X-STREAM-INF")) {
					m3u8Type = M3u8Content.M3U8_TYPE_RESOLUTION;
					resolution = new VDResolutionData.VDResolution();
					if (resolutionCount == 0) {
						resolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
					} else if (resolutionCount == 1) {
						resolutionTag = VDResolutionData.TYPE_DEFINITION_HD;
					} else if (resolutionCount == 2) {
						resolutionTag = VDResolutionData.TYPE_DEFINITION_FHD;
					} else if (resolutionCount == 3) {
						resolutionTag = VDResolutionData.TYPE_DEFINITION_3D;
					}
					resolution.setTag(resolutionTag);

					String program = findValueInString(line, "PROGRAM-ID=");
					if (program != null) {
						resolution.setProgramID(Integer.valueOf(program));
					}
					String bandWidth = findValueInString(line, "BANDWIDTH=");
					if (bandWidth != null) {
						resolution.setBandWidth(Integer.valueOf(bandWidth));
					}
					// idx++;
					resolutionCount++;
				} else if (line.startsWith("http://")) {
					if (m3u8Type == M3u8Content.M3U8_TYPE_RESOLUTION
							&& resolution != null) {
						resolution.setUrl(line);
						if (resolutionData == null) {
							resolutionData = new VDResolutionData();
						}
						resolutionData.addResolution(resolution);
						resolution = null;
					}
					// idx++;
				} else {
					resolution = null;
				}
			}

			// if (idx == 0) {
			// mListener.onError(ERROR_NO_CONTENT);
			// }
			if (m3u8Type == M3u8Content.M3U8_TYPE_RESOLUTION
					&& resolutionData != null) {
				return resolutionData;
			}
			return new VDResolutionData();
		} catch (IOException e) {
			LogS.d(TAG, "IOException " + e);
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {

				}
			}

		}
		return null;
	}

	private static String findValueInString(String line, String keyStr) {
		int keyStringPos = line.indexOf(keyStr);
		if (keyStringPos > 0) {
			int lastPos = line.indexOf(',', keyStringPos);
			if (lastPos < 0) {
				return line.substring(keyStringPos + keyStr.length());
			} else {
				return line.substring(keyStringPos + keyStr.length(), lastPos);
			}
		}
		return null;
	}

	public class M3u8AsyncTask extends AsyncTask<Void, Void, VDResolutionData> {

		private String mUrl;

		public M3u8AsyncTask(String url) {
			mUrl = url;
		}

		@Override
		protected void onPostExecute(VDResolutionData result) {
			if (mListener != null) {
				LogS.d(TAG, "parse result " + result);
				mListener.onParcelResult(mUrl, result);
			}
		}

		@Override
		protected VDResolutionData doInBackground(Void... params) {
			return retryConnect();
		}

		private VDResolutionData retryConnect() {
			int retryTimes = 0;
			for (int i = 0; i < RETRY_TIMES; i++) {
				LogS.d(TAG, "Retry time " + (i + 1));

				HttpGet request = null;
				try {
					LogS.d(TAG, "parse url " + mUrl);

					request = new HttpGet(mUrl);
					request.setHeader("Accept-Encoding", "gzip, deflate");
					request.setHeader("Accept-Language", "zh-cn");
					request.setHeader("Accept", "*/*");
					HttpResponse response = sHttpClient.execute(request);
					int statusCode = response.getStatusLine().getStatusCode();
					LogS.w(TAG, "M3u8Content retryConnect statusCode = "
							+ statusCode);
					if (statusCode == HttpStatus.SC_OK) {
						if (response.getEntity() != null) {
							LogS.d(TAG, "status OK, read file");
							return parse(response.getEntity().getContent());
						} else {
							LogS.e(TAG, "parse error");
							return null; // 提示错误
						}
					} else {
						mListener.onError(ERROR_PARSE);
						LogS.e(TAG, "parse error");
						return null; // 提示错误
					}
				} catch (ClientProtocolException e) {
					LogS.e(TAG, "ClientProtocolException " + e);
					e.printStackTrace();
				} catch (ConnectTimeoutException e) {
					// 如果是timeout，那么重试RETRY_TIMES次，失败了，就代表挂了。
					LogS.e(TAG, "ConnectTimeoutException " + e);
					e.printStackTrace();
					retryTimes++;
				} catch (ConnectException e) {
					LogS.e(TAG, "ConnectException " + e);
					e.printStackTrace();
				} catch (SocketTimeoutException e) {
					LogS.e(TAG, "SocketTimeoutException " + e);
					e.printStackTrace();
				} catch (SocketException e) {
					LogS.e(TAG, "SocketException " + e);
					e.printStackTrace();
				} catch (IOException e) {
					LogS.e(TAG, "IOException " + e);
					e.printStackTrace();
					return null; // 出错了就返回错误，不继续连接
				} finally {
				}
			}

			if (retryTimes >= 5) {
				mListener.onError(ERROR_PARSE);
				LogS.e(TAG, "parse error");
				return null; // 提示错误
			}

			return new VDResolutionData(); // 返回原地址
		}

	}

	private DefaultHttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params,
				CONNETED_TIMEOUT * 1000);
		HttpConnectionParams.setSoTimeout(params, CONNETED_TIMEOUT * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		ConnManagerParams.setMaxTotalConnections(params, 4);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(connMgr, params);
	}
}