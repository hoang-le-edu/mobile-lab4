# HƯỚNG DẪN TEST LAB 4 - BROADCASTRECEIVER

## Cấu trúc App

App đã được tổ chức thành menu với 2 bài:

### 1. **Lab06_1: Power State Change** (Bài 2)
- Test BroadcastReceiver lắng nghe sự kiện cắm/rút sạc pin
- Hiển thị Toast khi có sự kiện

### 2. **Lab06_3: Emergency SMS Response** (Bài 4)
- Test BroadcastReceiver nhận SMS có chứa "are you ok?"
- Tự động reply hoặc lưu danh sách số để reply sau

---

## CÁCH TEST TỪNG BÀI

### ✅ BÀI 1: Lab06_1 - Power State Change

**Bước 1:** Mở app, chọn "Lab06_1: Power State Change"

**Bước 2:** Test cắm/rút sạc trên Emulator:
```
1. Mở Extended Controls (3 chấm bên cạnh emulator)
2. Chọn "Battery"
3. Thay đổi "Charger connection" giữa "AC charger" và "None"
4. Quan sát Toast hiện lên với thông báo:
   - "Power connected" khi cắm sạc
   - "Power disconnected" khi rút sạc
```

**Lưu ý:** 
- BroadcastReceiver này đăng ký trong Manifest nên hoạt động ngay cả khi app bị tắt
- Có thể thoát app và test lại - vẫn sẽ thấy Toast

---

### ✅ BÀI 2: Lab06_3 - Emergency SMS Response

**Bước 1:** Mở app, chọn "Lab06_3: Emergency SMS Response"

**Bước 2:** Cấp quyền SMS (Rất quan trọng!)
```
1. Trong app, hệ thống sẽ yêu cầu cấp quyền SMS
2. Nếu không tự động hỏi, vào Settings > Apps > Lab4 > Permissions
3. Bật quyền: SMS, Phone
```

**Bước 3:** Gửi SMS test từ Emulator khác hoặc command line:

#### Option 1: Dùng Android Studio (Recommended)
```
1. Mở Extended Controls (... bên cạnh emulator)
2. Chọn Phone
3. Nhập số điện thoại: 5554 (hoặc số bất kỳ)
4. Nhập message: "are you ok?"
5. Click "Send Message"
```

#### Option 2: Dùng ADB command
```powershell
# Gửi SMS vào emulator
adb -s emulator-5554 emu sms send 5556 "are you ok?"

# Trong đó:
# - emulator-5554: tên device của bạn (xem bằng adb devices)
# - 5556: số điện thoại người gửi
# - "are you ok?": nội dung tin nhắn
```

#### Option 3: Dùng Telnet
```powershell
# Kết nối telnet
telnet localhost 5554

# Sau khi kết nối, gửi lệnh:
sms send 5556 "are you ok?"
```

**Bước 4:** Kiểm tra kết quả:

**TH1 - App đang MỞ (Lab06_3Activity đang chạy):**
- Số điện thoại sẽ xuất hiện trong ListView
- Nếu switch "Auto Response" BẬT → tự động gửi SMS reply
- Nếu switch "Auto Response" TẮT → hiển thị số trong list, bấm button để reply

**TH2 - App đang TẮT hoặc ở màn hình khác:**
- App sẽ tự động mở Lab06_3Activity
- Số điện thoại được thêm vào danh sách
- Xử lý tương tự TH1

**Bước 5:** Test các tính năng:

**A. Test Auto Response:**
```
1. Bật switch "Auto Response"
2. Gửi SMS "are you ok?"
3. Kiểm tra: App tự động reply "I am safe and well. Worry not!"
4. Số điện thoại tự động xóa khỏi list
```

**B. Test Manual Response:**
```
1. Tắt switch "Auto Response"
2. Gửi SMS "are you ok?" từ nhiều số khác nhau
3. Danh sách số sẽ hiện trong ListView
4. Bấm "I AM SAFE AND WELL" → gửi "I am safe and well. Worry not!" cho tất cả
5. Hoặc bấm "MAYDAY! MAYDAY! MAYDAY!" → gửi "Tell my mother I love her." cho tất cả
```

**C. Test với nhiều SMS:**
```
1. Gửi nhiều SMS "are you ok?" từ các số khác nhau:
   - adb -s emulator-5554 emu sms send 5556 "are you ok?"
   - adb -s emulator-5554 emu sms send 5557 "are you ok?"
   - adb -s emulator-5554 emu sms send 5558 "Are You OK?"  (test case-insensitive)
2. Tất cả số sẽ được thêm vào list (không trùng lặp)
```

---

## DEBUG - Xem Log

Nếu có vấn đề, xem log:

```powershell
# Xem tất cả log của app
adb logcat | Select-String "com.example.lab4"

# Xem log SMS received
adb logcat | Select-String "SmsReceiver"

# Xem log PowerState
adb logcat | Select-String "PowerStateChangeReceiver"
```

---

## LƯU Ý QUAN TRỌNG

### ⚠️ Permissions
- **PHẢI** cấp quyền SMS thủ công trên Android 6.0+
- Vào Settings > Apps > Lab4 > Permissions > Bật SMS

### ⚠️ Emulator
- Sử dụng emulator có Play Services để test SMS
- API Level 24+ recommended

### ⚠️ SMS Format
- Keyword: "are you ok?" (case-insensitive)
- Có thể viết: "Are You OK?", "ARE YOU OK?", etc.

### ⚠️ Testing Tips
- Test từng bài một để dễ debug
- PowerState: Dễ test nhất, test trước
- SMS: Phức tạp hơn, đảm bảo đã cấp quyền

---

## Kết quả mong đợi

✅ **Lab06_1:** Toast hiện ra mỗi khi cắm/rút sạc

✅ **Lab06_3:** 
- Nhận được SMS → số hiện trong list
- Auto response bật → tự động reply
- Auto response tắt → bấm button để reply
- App tắt → tự động mở lại khi nhận SMS

---

## Troubleshooting

**1. Không nhận được SMS:**
- Kiểm tra quyền SMS
- Xem logcat có báo lỗi không
- Thử gửi lại SMS với nội dung chính xác "are you ok?"

**2. Toast không hiện (PowerState):**
- Kiểm tra lại Charger connection trong Extended Controls
- Restart emulator

**3. App crash:**
- Xem logcat để biết lỗi cụ thể
- Kiểm tra đã cấp đủ permissions chưa

**4. SMS gửi đi không thấy:**
- Check trong app Messages của emulator
- Có thể emulator không hỗ trợ gửi SMS thực sự, chỉ log ra
