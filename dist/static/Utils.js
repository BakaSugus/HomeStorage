export function checkType(ext) {
  switch (ext.substring(ext.lastIndexOf(".")).toLowerCase()) {
    case ".jpg":
      return "picture";
    case ".png":
      return "picture";
      break;
    case ".gif":
      return "picture";
      break;
    case ".jpeg":
      return "picture";
      break;
    case ".mp4":
      return "video";
      break;
    case ".mkv":
      return "video";
      break;
    case ".avi":
      return "video";
      break;
    case ".pdf":
      return "pdf";
      break;
    case ".doc":
      return "MicrosoftOffice";
      break;
    case ".docx":
      return "MicrosoftOffice";
      break;
    case ".torrent":
      return "torrent";
      break;
    case ".mp3":
      return "music";
      break;
  }
}

export function checkWithType(type){
    console.log(type);
}

export function parseDate(str) {
  return new Date(value);
}

export function byteToSize(bytes) {
  //KB MB GB
  if (bytes === 0) return '0 B';
  var k = 1024, // or 1024
    sizes = ['B','KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
    i = Math.floor(Math.log(bytes) / Math.log(k));

  return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
}
