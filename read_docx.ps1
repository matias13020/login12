$file = Get-ChildItem "C:\Users\Lenovo\Downloads" -Filter "TI3042*.docx" | Select-Object -First 1
if (-not $file) {
    Write-Error "File not found"
    exit
}
Write-Output "Found file: $($file.FullName)"
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($file.FullName)
$entry = $zip.GetEntry("word/document.xml")
$sr = New-Object System.IO.StreamReader($entry.Open())
$xml = $sr.ReadToEnd()
$sr.Close()
$zip.Dispose()

# Simple regex to extract text inside <w:t>...</w:t> elements
$matches = [regex]::Matches($xml, '<w:t[^>]*>(.*?)</w:t>')
foreach ($m in $matches) {
    $m.Groups[1].Value
}
